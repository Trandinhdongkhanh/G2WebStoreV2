package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.AddProductsToShopCateRequest;
import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.enums.ShopProductsSortType;
import com.hcmute.g2webstorev2.enums.SortType;
import com.hcmute.g2webstorev2.es.index.ProductIndex;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.exception.SellFunctionLockedException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.*;
import com.hcmute.g2webstorev2.service.ElasticSearchService;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.ProductService;
import com.hcmute.g2webstorev2.util.ProductUtil;
import com.hcmute.g2webstorev2.util.ReviewUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepo productRepo;
    private final ShopRepo shopRepo;
    private final CategoryRepo categoryRepo;
    private final FileService fileService;
    private final ShopCateRepo shopCateRepo;
    private final ShopItemRepo shopItemRepo;
    private final ElasticSearchService esService;
    private final ProductESRepo productESRepo;
    private final ReviewRepo reviewRepo;
    private final GCPFileRepo gcpFileRepo;

    @Override
    @Transactional
    public void updateProducts(List<Product> products) {
        productRepo.saveAll(products);
        productESRepo.saveAll(products.stream().map(Mapper::toProductIndex).toList());
    }

    private boolean isInProvince(ProductIndex productIndex, Integer provinceId) {
        Product product = productRepo.findById(productIndex.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return product.getShop().getProvinceId().equals(provinceId);
    }

    @Override
    public Page<ProductIndex> getProductsByName(String name, int pageNumber, int pageSize, Integer seed,
                                                SortType sortType, Integer startPrice, Integer endPrice,
                                                Integer provinceId, Integer star) throws IOException {
        List<ProductIndex> products;
        if (sortType.equals(SortType.MOST_RELEVANT))
            products = ProductUtil.convertToList(esService.boolSearchProducts(name, null));
        else
            products = ProductUtil.convertToList(esService.boolSearchProducts(name, seed));

        if (startPrice != null && endPrice != null)
            products = products.stream()
                    .filter(product -> product.getPrice() >= startPrice && product.getPrice() <= endPrice)
                    .collect(Collectors.toList());
        if (provinceId != null)
            products = products.stream()
                    .filter(product -> isInProvince(product, provinceId))
                    .collect(Collectors.toList());
        if (star != null)
            products = products.stream()
                    .filter(product -> isAboveStar(product, star))
                    .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        switch (sortType) {
            case NEWEST -> {
                products.sort(Comparator.comparingInt(ProductIndex::getProductId).reversed());
                return new PageImpl<>(ProductUtil.getPageContent(products, pageable), pageable, products.size());
            }
            case TOP_SELLER -> {
                products.sort(Comparator.comparingInt(ProductIndex::getSoldQuantity).reversed());
                return new PageImpl<>(ProductUtil.getPageContent(products, pageable), pageable, products.size());
            }
            case PRICE_DESC -> {
                products.sort(Comparator.comparingInt(ProductIndex::getPrice).reversed());
                return new PageImpl<>(ProductUtil.getPageContent(products, pageable), pageable, products.size());
            }
            case PRICE_ASC -> {
                products.sort(Comparator.comparingInt(ProductIndex::getPrice));
                return new PageImpl<>(ProductUtil.getPageContent(products, pageable), pageable, products.size());
            }
            case DEFAULT, MOST_RELEVANT -> {
                return new PageImpl<>(ProductUtil.getPageContent(products, pageable), pageable, products.size());
            }
        }
        return new PageImpl<>(ProductUtil.getPageContent(products, pageable), pageable, products.size());
    }

    @Override
    public Page<ProductIndex> getAllProducts(int pageNumber, int pageSize, Integer seed,
                                             SortType sortType, Integer startPrice, Integer endPrice,
                                             Integer provinceId, Integer star) {

        List<Product> products = filterProducts(startPrice, endPrice, provinceId, star, productRepo.findAll());
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        if (sortType != null) {
            switch (sortType) {
                case DEFAULT -> {
                    Collections.shuffle(products, new Random(seed));
                    return new PageImpl<>(ProductUtil.getContent(products, pageable), pageable, products.size())
                            .map(Mapper::toProductIndex);
                }
                case NEWEST -> {
                    products.sort(Comparator.comparingInt(Product::getProductId).reversed());
                    return new PageImpl<>(ProductUtil.getContent(products, pageable), pageable, products.size())
                            .map(Mapper::toProductIndex);
                }
                case PRICE_ASC -> {
                    products.sort(Comparator.comparingInt(Product::getPrice));
                    return new PageImpl<>(ProductUtil.getContent(products, pageable), pageable, products.size())
                            .map(Mapper::toProductIndex);
                }
                case PRICE_DESC -> {
                    products.sort(Comparator.comparingInt(Product::getPrice).reversed());
                    return new PageImpl<>(ProductUtil.getContent(products, pageable), pageable, products.size())
                            .map(Mapper::toProductIndex);
                }
                case TOP_SELLER -> {
                    products.sort(Comparator.comparingInt(Product::getSoldQuantity).reversed());
                    return new PageImpl<>(ProductUtil.getContent(products, pageable), pageable, products.size())
                            .map(Mapper::toProductIndex);
                }
            }
        }
        Collections.shuffle(products, new Random(seed));
        return new PageImpl<>(ProductUtil.getContent(products, pageable), pageable, products.size())
                .map(Mapper::toProductIndex);
    }

    private List<Product> filterProducts(Integer startPrice, Integer endPrice,
                                         Integer provinceId, Integer star, List<Product> products) {
        if (startPrice != null && endPrice != null)
            products = products.stream()
                    .filter(product -> product.getPrice() >= startPrice && product.getPrice() <= endPrice)
                    .collect(Collectors.toList());
        if (provinceId != null)
            products = products.stream()
                    .filter(product -> product.getShop().getProvinceId().equals(provinceId))
                    .collect(Collectors.toList());
        if (star != null)
            products = products.stream()
                    .filter(product -> isAboveStar(product, star))
                    .collect(Collectors.toList());
        return products;
    }

    @Override
    public ProductResponse getProduct(Integer id) {
        return Mapper.toProductResponse(productRepo.findById(id)
                .filter(Product::getIsAvailable)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found")));
    }

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest body, MultipartFile[] files) throws IOException {
        log.info("Beginning add product...");
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!seller.getShop().getIsAllowedToSell())
            throw new SellFunctionLockedException("Your shop is currently banned from selling products");
        if (productRepo.existsByNameAndShop(body.getName(), seller.getShop()))
            throw new ResourceNotUniqueException("Duplicate product name");

        Category category = categoryRepo.findById(body.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category with ID = " + body.getCategoryId() + " not found"));

        Product product = Product.builder()
                .name(body.getName())
                .description(body.getDescription())
                .price(body.getPrice())
                .stockQuantity(body.getStockQuantity())
                .category(category)
                .shop(seller.getShop())
                .soldQuantity(0)
                .height(body.getHeight())
                .weight(body.getWeight())
                .width(body.getWidth())
                .length(body.getLength())
                .isAvailable(true)
                .isBanned(false)
                .build();

        List<GCPFile> images = fileService.uploadFiles(files);
        images.forEach(img -> img.setProduct(product));
        product.setImages(images);

        Product res = productRepo.save(product);
        ProductIndex productIndex = Mapper.toProductIndex(res);
        productESRepo.save(productIndex);

        log.info("Product with ID = " + res.getProductId() + " have been created");
        return Mapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse enableProduct(boolean isAvailable, Integer productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getIsBanned())
            throw new SellFunctionLockedException(
                    "Product is banned, please adjust your product and wait for admin to review it");
        if (!isAvailable) shopItemRepo.deleteAllByProduct(product);
        product.setIsAvailable(isAvailable);
        productESRepo.save(Mapper.toProductIndex(product));
        return Mapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse bannedProduct(boolean isBanned, Integer productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Shop shop = product.getShop();
        if (isBanned) {
            shopItemRepo.deleteAllByProduct(product);
            product.setIsAvailable(false);
            shop.setViolationPoint(shop.getViolationPoint() + 1);
        }
        shopRepo.save(shop);
        product.setIsBanned(isBanned);
        productESRepo.save(Mapper.toProductIndex(product));
        return Mapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public void updateProduct(ProductRequest body, Integer id, MultipartFile[] files) {
        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product with ID = " + id + " not found"));

        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(product.getShop().getShopId(), seller.getShop().getShopId()))
            throw new AccessDeniedException("Access denied, you don't have permission on this product");

        if (!Objects.equals(body.getName(), product.getName()) &&
                productRepo.existsByNameAndShop(body.getName(), seller.getShop()))
            throw new ResourceNotUniqueException("Duplicate product name");

        Category category = categoryRepo.findById(body.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category with ID = " + body.getCategoryId() + " not found"));

        if (files != null && files.length != 0) {
            List<GCPFile> images = fileService.uploadFiles(files);
            images.forEach(img -> img.setProduct(product));
            product.setImages(images);
        }

        product.setName(body.getName());
        product.setDescription(body.getDescription());
        product.setPrice(body.getPrice());
        product.setStockQuantity(body.getStockQuantity());
        product.setCategory(category);
        product.setHeight(body.getHeight());
        product.setWeight(body.getWeight());
        product.setWidth(body.getWidth());
        product.setLength(body.getLength());
        productESRepo.save(Mapper.toProductIndex(product));

        log.info("Product with ID = " + id + " updated successfully");
    }

    @Override
    @Transactional
    public void delProduct(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));

        productRepo.delete(product);
        productESRepo.delete(Mapper.toProductIndex(product));

        log.info("Product with ID = " + id + " deleted successfully");
    }

    @Override
    public Page<ProductResponse> sellerGetAllProductsByShop(Integer pageNumber, Integer pageSize,
                                                            ShopProductsSortType sortType) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Shop shop = seller.getShop();

        if (sortType != null) {
            switch (sortType) {
                case DEFAULT -> {
                    return productRepo.sellerFindAllByShop(
                            shop,
                            PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending())
                    ).map(Mapper::toProductResponse);
                }
                case STOCK_QUANTITY_DESC -> {
                    return productRepo.sellerFindAllByShop(
                            shop,
                            PageRequest.of(pageNumber, pageSize, Sort.by("stockQuantity").descending())
                    ).map(Mapper::toProductResponse);
                }
                case STOCK_QUANTITY_ASC -> {
                    return productRepo.sellerFindAllByShop(
                            shop,
                            PageRequest.of(pageNumber, pageSize, Sort.by("stockQuantity").ascending())
                    ).map(Mapper::toProductResponse);
                }
                case SOLD_QUANTITY_ASC -> {
                    return productRepo.sellerFindAllByShop(
                            shop,
                            PageRequest.of(pageNumber, pageSize, Sort.by("soldQuantity").ascending())
                    ).map(Mapper::toProductResponse);
                }
                case SOLD_QUANTITY_DESC -> {
                    return productRepo.sellerFindAllByShop(
                            shop,
                            PageRequest.of(pageNumber, pageSize, Sort.by("soldQuantity").descending())
                    ).map(Mapper::toProductResponse);
                }
            }
        }
        return productRepo.sellerFindAllByShop(
                shop,
                PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending())
        ).map(Mapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> customerGetAllProductsByShop(Integer shopId, SortType sortType, int page, int size,
                                                              Integer startPrice, Integer endPrice, String name,
                                                              Integer shopCateId) {
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        List<Product> products = productRepo.customerFindAllByShop(shop);
        if (name != null) products = productRepo.customerFindAllByShopAndName(shop, name);
        if (startPrice != null && endPrice != null)
            products = products.stream()
                    .filter(product -> product.getPrice() >= startPrice && product.getPrice() <= endPrice)
                    .collect(Collectors.toList());
        if (shopCateId != null)
            products = products.stream()
                    .filter(product -> ProductUtil.isBelongToShopCate(product, shopCateId))
                    .collect(Collectors.toList());
        PageRequest pageable = PageRequest.of(page, size);

        switch (sortType) {
            case DEFAULT -> {
                Collections.shuffle(products, new Random(5));
                return new PageImpl<>(
                        ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductResponse).toList(),
                        pageable,
                        products.size());
            }
            case TOP_SELLER -> {
                products.sort(Comparator.comparingInt(Product::getSoldQuantity).reversed());
                return new PageImpl<>(
                        ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductResponse).toList(),
                        pageable,
                        products.size());
            }
            case NEWEST -> {
                products.sort(Comparator.comparingInt(Product::getProductId).reversed());
                return new PageImpl<>(
                        ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductResponse).toList(),
                        pageable,
                        products.size());
            }
            case PRICE_DESC -> {
                products.sort(Comparator.comparingInt(Product::getPrice).reversed());
                return new PageImpl<>(
                        ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductResponse).toList(),
                        pageable,
                        products.size());
            }
            case PRICE_ASC -> {
                products.sort(Comparator.comparingInt(Product::getPrice));
                return new PageImpl<>(
                        ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductResponse).toList(),
                        pageable,
                        products.size());
            }
        }
        return new PageImpl<>(
                ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductResponse).toList(),
                pageable,
                products.size());
    }

    @Override
    public Page<ProductIndex> getProductsByCategory(Integer id, int pageNumber, int pageSize, Integer seed,
                                                    SortType sortType, Integer startPrice, Integer endPrice,
                                                    Integer provinceId, Integer star) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        List<Product> products = productRepo.findAllByCategory(ProductUtil.getPath(category));
        products = filterProducts(startPrice, endPrice, provinceId, star, products);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        if (sortType != null) {
            switch (sortType) {
                case NEWEST -> {
                    products.sort(Comparator.comparingInt(Product::getProductId).reversed());
                    return new PageImpl<>(
                            ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductIndex).toList(),
                            pageable,
                            products.size());
                }
                case TOP_SELLER -> {
                    products.sort(Comparator.comparingInt(Product::getSoldQuantity));
                    return new PageImpl<>(
                            ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductIndex).toList(),
                            pageable,
                            products.size());
                }
                case PRICE_DESC -> {
                    products.sort(Comparator.comparingInt(Product::getPrice).reversed());
                    return new PageImpl<>(
                            ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductIndex).toList(),
                            pageable,
                            products.size());
                }
                case PRICE_ASC -> {
                    products.sort(Comparator.comparingInt(Product::getPrice));
                    return new PageImpl<>(
                            ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductIndex).toList(),
                            pageable,
                            products.size());
                }
                case DEFAULT -> {
                    Collections.shuffle(products, new Random(seed));
                    return new PageImpl<>(
                            ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductIndex).toList(),
                            pageable,
                            products.size());
                }
            }
        }
        Collections.shuffle(products, new Random(seed));
        return new PageImpl<>(
                ProductUtil.getContent(products, pageable).stream().map(Mapper::toProductIndex).toList(),
                pageable,
                products.size());
    }


    private boolean isAboveStar(Product product, Integer star) {
        long totalRateCount = reviewRepo.countReviewsByProduct(product.getProductId());
        Long totalRateValue = reviewRepo.sumOfRatesByProduct(product.getProductId());
        return ReviewUtil.getAvgRate(totalRateValue, totalRateCount) >= star;
    }

    private boolean isAboveStar(ProductIndex productIndex, Integer star) {
        long totalRateCount = reviewRepo.countReviewsByProduct(productIndex.getProductId());
        Long totalRateValue = reviewRepo.sumOfRatesByProduct(productIndex.getProductId());
        return ReviewUtil.getAvgRate(totalRateValue, totalRateCount) >= star;
    }

    @Override
    @Transactional
    public void addProductsToShopCate(Integer shopCateId, AddProductsToShopCateRequest body) {
        ShopCategory shopCategory = shopCateRepo.findById(shopCateId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop category with ID = " + shopCateId + " not found"));

        List<Product> products = new ArrayList<>();

        body.getIds().forEach(id -> {
            Product product = productRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));

            product.setShopCategory(shopCategory);
            products.add(product);
        });

        List<Product> results = productRepo.saveAll(products);
        productESRepo.saveAll(results.stream().map(Mapper::toProductIndex).toList());
    }

    @Override
    public Page<ProductResponse> getProductsByShopCate(Integer id, int pageNumber, int pageSize) {
        ShopCategory shopCategory = shopCateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop category with ID = " + id + " not found"));

        return productRepo.findAllByShopCategory(shopCategory, PageRequest.of(pageNumber, pageSize))
                .map(Mapper::toProductResponse);
    }

    @Override
    public List<ProductResponse> getTopFivePopularProductByShop(Integer shopId) {
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        return productRepo.sellerFindAllByShop(shop, PageRequest.of(
                        0,
                        5,
                        Sort.by("soldQuantity").descending()))
                .map(Mapper::toProductResponse).getContent();
    }

    @Override
    @Transactional
    public ProductResponse delImage(Integer productId, Long fileId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        GCPFile gcpFile = gcpFileRepo.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));
        product.getImages().remove(gcpFile);
        gcpFile.setProduct(null);
        Product result = productRepo.save(product);
        productESRepo.save(Mapper.toProductIndex(result));
        return Mapper.toProductResponse(result);
    }
}