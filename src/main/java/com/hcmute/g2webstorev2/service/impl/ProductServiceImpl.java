package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.AddProductsToShopCateRequest;
import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.enums.ShopProductsSortType;
import com.hcmute.g2webstorev2.enums.SortType;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.exception.SellFunctionLockedException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.*;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    @Transactional
    public void updateProducts(List<Product> products) {
        productRepo.saveAll(products);
    }

    @Override
    public Page<ProductResponse> getProductsByName(String name, int pageNumber, int pageSize, Integer seed, SortType sortType,
                                                   Integer startPrice, Integer endPrice, Integer districtId) {
        if (sortType != null) {
            switch (sortType) {
                case NEWEST -> {
                    return getNewestProductsByName(name, startPrice, endPrice, pageNumber, pageSize);
                }
                case TOP_SELLER -> {
                    return getTopSellProductsByName(name, startPrice, endPrice, pageNumber, pageSize);
                }
                case PRICE_DESC -> {
                    return getProductsByNameAndPriceDesc(name, startPrice, endPrice, pageNumber, pageSize);
                }
                case PRICE_ASC -> {
                    return getProductsByNameAndPriceAsc(name, startPrice, endPrice, pageNumber, pageSize);
                }
                case DEFAULT -> {
                    return getDefaultProductsByName(name, startPrice, endPrice, pageNumber, pageSize, seed);
                }
            }
        }
        return getDefaultProductsByName(name, startPrice, endPrice, pageNumber, pageSize, seed);
    }

    private Page<ProductResponse> getDefaultProductsByName(String name, Integer startPrice, Integer endPrice,
                                                           int pageNumber, int pageSize, int seed) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByNameAndPriceBetween(
                    name,
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize),
                    seed
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByName(
                name,
                PageRequest.of(pageNumber, pageSize),
                seed
        ).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getProductsByNameAndPriceAsc(String name, Integer startPrice, Integer endPrice,
                                                               int pageNumber, int pageSize) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByNameAndPriceBetween(
                    name,
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize, Sort.by("price").ascending())
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByName(
                name,
                PageRequest.of(pageNumber, pageSize, Sort.by("price").ascending())
        ).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getProductsByNameAndPriceDesc(String name, Integer startPrice, Integer endPrice,
                                                                int pageNumber, int pageSize) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByNameAndPriceBetween(
                    name,
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize, Sort.by("price").descending())
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByName(
                name,
                PageRequest.of(pageNumber, pageSize, Sort.by("price").descending())
        ).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getNewestProductsByName(String name, Integer startPrice, Integer endPrice,
                                                          int pageNumber, int pageSize) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByNameAndPriceBetween(
                    name,
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending())
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByName(
                name,
                PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending())
        ).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getTopSellProductsByName(String name, Integer startPrice, Integer endPrice,
                                                           int pageNumber, int pageSize) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByNameAndPriceBetween(
                    name,
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize, Sort.by("soldQuantity").descending())
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByName(
                name,
                PageRequest.of(pageNumber, pageSize, Sort.by("soldQuantity").descending())
        ).map(Mapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, Integer seed, SortType sortType,
                                                Integer startPrice, Integer endPrice, Integer districtId) {
        if (sortType != null) {
            switch (sortType) {
                case DEFAULT -> {
                    return getDefaultProducts(pageNumber, pageSize, seed, startPrice, endPrice, districtId);
                }
                case NEWEST -> {
                    return getNewestProducts(pageNumber, pageSize, startPrice, endPrice, districtId);
                }
                case PRICE_ASC -> {
                    return getProductsByPriceAsc(pageNumber, pageSize, startPrice, endPrice, districtId);
                }
                case PRICE_DESC -> {
                    return getProductsByPriceDesc(pageNumber, pageSize, startPrice, endPrice, districtId);
                }
                case TOP_SELLER -> {
                    return getTopSellProducts(pageNumber, pageSize, startPrice, endPrice, districtId);
                }
            }
        }
        return getDefaultProducts(pageNumber, pageSize, seed, startPrice, endPrice, districtId);
    }

    private Page<ProductResponse> getTopSellProducts(int pageNumber, int pageSize, Integer startPrice,
                                                     Integer endPrice, Integer districtId) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByPriceBetween(
                            startPrice,
                            endPrice,
                            PageRequest.of(pageNumber, pageSize, Sort.by("soldQuantity").descending()))
                    .map(Mapper::toProductResponse);

        return productRepo.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("soldQuantity").descending()))
                .map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getProductsByPriceDesc(int pageNumber, int pageSize, Integer startPrice,
                                                         Integer endPrice, Integer districtId) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByPriceBetween(
                            startPrice,
                            endPrice,
                            PageRequest.of(pageNumber, pageSize, Sort.by("price").descending()))
                    .map(Mapper::toProductResponse);

        return productRepo.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("price").descending()))
                .map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getProductsByPriceAsc(int pageNumber, int pageSize, Integer startPrice,
                                                        Integer endPrice, Integer districtId) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByPriceBetween(
                            startPrice,
                            endPrice,
                            PageRequest.of(pageNumber, pageSize, Sort.by("price").ascending()))
                    .map(Mapper::toProductResponse);

        return productRepo.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("price").ascending()))
                .map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getNewestProducts(int pageNumber, int pageSize, Integer startPrice, Integer endPrice,
                                                    Integer districtId) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByPriceBetween(
                            startPrice,
                            endPrice,
                            PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending()))
                    .map(Mapper::toProductResponse);

        return productRepo.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending()))
                .map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getDefaultProducts(int pageNumber, int pageSize, Integer seed, Integer startPrice,
                                                     Integer endPrice, Integer districtId) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByPriceBetween(startPrice, endPrice, seed, PageRequest.of(pageNumber, pageSize))
                    .map(Mapper::toProductResponse);

        return productRepo.findAll(seed, PageRequest.of(pageNumber, pageSize)).map(Mapper::toProductResponse);
    }

    @Override
    public ProductResponse getProduct(Integer id) {
        return Mapper.toProductResponse(productRepo.findById(id)
                .filter(Product::getIsAvailable)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found")));
    }

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest body, MultipartFile[] files) {
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

        ProductResponse res = Mapper.toProductResponse(productRepo.save(product));

        log.info("Product with ID = " + res.getProductId() + " have been created");
        return res;
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

        log.info("Product with ID = " + id + " updated successfully");
    }

    @Override
    @Transactional
    public void delProduct(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));

        productRepo.delete(product);

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
    public Page<ProductResponse> customerGetAllProductsByShop(Integer shopId, SortType sortType, int page, int size) {
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        switch (sortType) {
            case DEFAULT -> {
                return productRepo.customerFindAllByShop(shop, PageRequest.of(page, size))
                        .map(Mapper::toProductResponse);
            }
            case TOP_SELLER -> {
                return productRepo.customerFindAllByShop(
                        shop,
                        PageRequest.of(page, size, Sort.by("soldQuantity").descending())
                ).map(Mapper::toProductResponse);
            }
            case NEWEST -> {
                return productRepo.customerFindAllByShop(
                        shop,
                        PageRequest.of(page, size, Sort.by("productId").descending())
                ).map(Mapper::toProductResponse);
            }
            case PRICE_DESC -> {
                return productRepo.customerFindAllByShop(
                        shop,
                        PageRequest.of(page, size, Sort.by("price").descending())
                ).map(Mapper::toProductResponse);
            }
            case PRICE_ASC -> {
                return productRepo.customerFindAllByShop(
                        shop,
                        PageRequest.of(page, size, Sort.by("price").ascending())
                ).map(Mapper::toProductResponse);
            }
        }
        return productRepo.customerFindAllByShop(shop, PageRequest.of(page, size)).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getNewestProductsByCategory(Integer id, Integer startPrice, Integer endPrice,
                                                              int pageNumber, int pageSize) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByCategoryAndPriceBetween(
                    getPath(id),
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending())
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByCategory(
                getPath(id),
                PageRequest.of(pageNumber, pageSize, Sort.by("productId").descending())
        ).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getTopSellProductsByCategory(Integer id, Integer startPrice, Integer endPrice,
                                                               int pageNumber, int pageSize) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByCategoryAndPriceBetween(
                    getPath(id),
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize, Sort.by("soldQuantity").descending())
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByCategory(
                getPath(id),
                PageRequest.of(pageNumber, pageSize, Sort.by("soldQuantity").descending())
        ).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getProductsByCategoryAndPriceDesc(Integer id, Integer startPrice, Integer endPrice,
                                                                    int pageNumber, int pageSize) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByCategoryAndPriceBetween(
                    getPath(id),
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize, Sort.by("price").descending())
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByCategory(
                getPath(id),
                PageRequest.of(pageNumber, pageSize, Sort.by("price").descending())
        ).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getProductsByCategoryAndPriceAsc(Integer id, Integer startPrice, Integer endPrice,
                                                                   int pageNumber, int pageSize) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByCategoryAndPriceBetween(
                    getPath(id),
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize, Sort.by("price").ascending())
            ).map(Mapper::toProductResponse);

        return productRepo.findAllByCategory(
                getPath(id),
                PageRequest.of(pageNumber, pageSize, Sort.by("price").ascending())
        ).map(Mapper::toProductResponse);
    }

    private Page<ProductResponse> getDefaultProductsByCategory(Integer id, Integer startPrice, Integer endPrice,
                                                               int pageNumber, int pageSize, int seed) {
        if (startPrice != null && endPrice != null)
            return productRepo.findAllByCategoryAndPriceBetween(
                    getPath(id),
                    startPrice,
                    endPrice,
                    PageRequest.of(pageNumber, pageSize),
                    seed
            ).map(Mapper::toProductResponse);

        return productRepo
                .findAllByCategory(getPath(id), PageRequest.of(pageNumber, pageSize), seed)
                .map(Mapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(Integer id, int pageNumber, int pageSize, Integer seed,
                                                       SortType sortType, Integer startPrice, Integer endPrice,
                                                       Integer districtId) {
        if (sortType != null) {
            switch (sortType) {
                case NEWEST -> {
                    return getNewestProductsByCategory(id, startPrice, endPrice, pageNumber, pageSize);
                }
                case TOP_SELLER -> {
                    return getTopSellProductsByCategory(id, startPrice, endPrice, pageNumber, pageSize);
                }
                case PRICE_DESC -> {
                    return getProductsByCategoryAndPriceDesc(id, startPrice, endPrice, pageNumber, pageSize);
                }
                case PRICE_ASC -> {
                    return getProductsByCategoryAndPriceAsc(id, startPrice, endPrice, pageNumber, pageSize);
                }
                case DEFAULT -> {
                    return getDefaultProductsByCategory(id, startPrice, endPrice, pageNumber, pageSize, seed);
                }
            }
        }
        return getDefaultProductsByCategory(id, startPrice, endPrice, pageNumber, pageSize, seed);
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

        productRepo.saveAll(products);
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

    private String getPath(Integer id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + id + " not found"));

        String path;

        if (category.getChildCategories().isEmpty()) path = category.getPath();
        else path = category.getPath() + "/";

        return path;
    }
}