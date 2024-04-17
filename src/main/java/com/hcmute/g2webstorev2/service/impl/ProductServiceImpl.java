package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.entity.Category;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CategoryRepo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ShopRepo shopRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public Page<ProductResponse> getAllProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Product> products = new ArrayList<>();

        shopRepo.findAll().forEach(shop -> {
            Product product = productRepo.findByShopRandom(shop.getShopId());
            products.add(product);
        });

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());

        List<Product> pageContent = products.subList(start, end);
        return new PageImpl<>(pageContent, pageable, products.size()).map(Mapper::toProductResponse);
    }

    @Override
    public ProductResponse getProduct(Integer id) {
        return Mapper.toProductResponse(productRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product with ID = " + id + " not found")));
    }

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest body) {
        log.info("Beginning add product...");
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (productRepo.existsByNameAndShop(body.getName(), seller.getShop()))
            throw new ResourceNotUniqueException("Duplicate product name");

        Category category = categoryRepo.findById(body.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category with ID = " + body.getCategoryId() + " not found"));

        ProductResponse res = Mapper.toProductResponse(productRepo.save(
                Product.builder()
                        .name(body.getName())
                        .images(body.getImages())
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
                        .build()
        ));

        log.info("Product with ID = " + res.getProductId() + " have been created");
        return res;
    }

    @Override
    @Transactional
    public void updateProduct(ProductRequest body, Integer id) {
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

        product.setName(body.getName());
        product.setImages(body.getImages());
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
    public List<ProductResponse> getAllProductsByShop(Integer id) {
        Shop shop = shopRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop with ID = " + id + " not found"));
        return productRepo.findAllByShop(shop)
                .stream().map(Mapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> getAllRandomProducts(Integer pageNumber) {
        return null;
    }
}
