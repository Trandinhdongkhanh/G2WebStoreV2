package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.enums.SortType;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CategoryRepo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ShopRepo shopRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private FileService fileService;

    @Override
    public Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, Integer seed) {
        return productRepo.findRandomProducts(seed, PageRequest.of(pageNumber, pageSize))
                .map(Mapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByName(int pageNumber, int pageSize, String name, Integer seed) {
        return productRepo.findRandomProductsByName(seed, PageRequest.of(pageNumber, pageSize), name)
                .map(Mapper::toProductResponse);
    }

    @Override
    public ProductResponse getProduct(Integer id) {
        return Mapper.toProductResponse(productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found")));
    }

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest body, MultipartFile[] files) {
        log.info("Beginning add product...");
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (productRepo.existsByNameAndShop(body.getName(), seller.getShop()))
            throw new ResourceNotUniqueException("Duplicate product name");

        Category category = categoryRepo.findById(body.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category with ID = " + body.getCategoryId() + " not found"));

        Product product = Product.builder()
                .name(body.getName())
                .description(body.getDescription())
                .price(body.getPrice())
                .specialPrice(body.getSpecialPrice())
                .stockQuantity(body.getStockQuantity())
                .category(category)
                .shop(seller.getShop())
                .soldQuantity(0)
                .height(body.getHeight())
                .weight(body.getWeight())
                .width(body.getWidth())
                .length(body.getLength())
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
        product.setSpecialPrice(body.getSpecialPrice());
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

        log.info("Product with ID = " + id + "deleted successfully");
    }

    @Override
    public Page<ProductResponse> getAllProductsByShop(Integer id, Integer pageNumber, Integer pageSize) {
        Shop shop = shopRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop with ID = " + id + " not found"));

        return productRepo.findAllByShop(shop, PageRequest.of(pageNumber, pageSize))
                .map(Mapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(Integer id, int pageNumber, int pageSize, Integer seed,
                                                       SortType sortType, Integer startPrice, Integer endPrice,
                                                       Integer districtId) {

        if (sortType != null && startPrice == null && endPrice == null && districtId == null)
            return this.getProductsByCategory(id ,pageNumber, pageSize, sortType, seed);

        if (sortType != null && startPrice != null && endPrice != null && districtId == null)
            return this.getProductsByCategory(id, pageNumber, pageSize, sortType, seed ,startPrice, endPrice);

        return productRepo.findAllByCategory(this.getPath(id), PageRequest.of(pageNumber, pageSize), seed)
                .map(Mapper::toProductResponse);
    }

    private String getPath(Integer id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + id + " not found"));

        String path;

        if (category.getChildCategories().isEmpty()) path = category.getPath();
        else path = category.getPath() + "/";

        return path;
    }

    private Page<ProductResponse> getProductsByCategory(Integer id, int pageNumber, int pageSize,
                                                        SortType sortType, Integer seed, Integer starPrice, Integer endPrice) {

        String path = this.getPath(id);

        switch (sortType){
            case PRICE_ASC -> {
                return productRepo.findAllByCategoryOrderByPriceAsc(
                        path,
                        PageRequest.of(pageNumber, pageSize),
                        starPrice,
                        endPrice
                ).map(Mapper::toProductResponse);
            }
            case PRICE_DESC -> {
                return productRepo.findAllByCategoryOrderByPriceDesc(
                        path,
                        PageRequest.of(pageNumber, pageSize),
                        starPrice,
                        endPrice
                ).map(Mapper::toProductResponse);
            }
            case TOP_SELLER -> {
                return productRepo.findTopSellByCategory(
                        path,
                        PageRequest.of(pageNumber, pageSize),
                        starPrice,
                        endPrice
                ).map(Mapper::toProductResponse);
            }
            case NEWEST -> {
                return productRepo.findNewestByCategory(
                        path,
                        PageRequest.of(pageNumber, pageSize),
                        starPrice,
                        endPrice
                ).map(Mapper::toProductResponse);
            }
            case DEFAULT -> {
                return productRepo.findAllByCategory(
                        path,
                        PageRequest.of(pageNumber, pageSize),
                        starPrice,
                        endPrice,
                        seed
                ).map(Mapper::toProductResponse);
            }
        }

        return productRepo.findProductsByCategoryWherePriceBetween(
                path,
                PageRequest.of(pageNumber, pageSize),
                starPrice,
                endPrice,
                seed
        ).map(Mapper::toProductResponse);
    }


    private Page<ProductResponse> getProductsByCategory(Integer id, int pageNumber, int pageSize,
                                                        SortType sortType, Integer seed) {
        String path = this.getPath(id);

        switch (sortType) {
            case DEFAULT -> {
                return productRepo.findAllByCategory(path, PageRequest.of(pageNumber, pageSize), seed)
                        .map(Mapper::toProductResponse);
            }
            case PRICE_DESC -> {
                return productRepo.findAllByCategoryOrderByPriceDesc(path, PageRequest.of(pageNumber, pageSize))
                        .map(Mapper::toProductResponse);
            }
            case PRICE_ASC -> {
                return productRepo.findAllByCategoryOrderByPriceAsc(path, PageRequest.of(pageNumber, pageSize))
                        .map(Mapper::toProductResponse);
            }
            case NEWEST -> {
                return productRepo.findNewestByCategory(path, PageRequest.of(pageNumber, pageSize))
                        .map(Mapper::toProductResponse);
            }
            case TOP_SELLER -> {
                return productRepo.findTopSellByCategory(path, PageRequest.of(pageNumber, pageSize))
                        .map(Mapper::toProductResponse);
            }
        }

        return productRepo.findAllByCategory(path, PageRequest.of(pageNumber, pageSize), seed)
                .map(Mapper::toProductResponse);
    }
}
