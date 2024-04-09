package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.entity.Category;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CategoryRepo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll()
                .stream()
                .map(Mapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProduct(Integer id) {
        return Mapper.toProductResponse(productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found")));
    }

    @Override
    @Transactional
    public ProductResponse addProduct(ProductRequest body) {
        Shop shop = shopRepo.findById(body.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop with ID = " + body.getShopId() + " not found"));
        Category category = categoryRepo.findById(body.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + body.getCategoryId() + " not found"));

        log.info("Adding product...");

        ProductResponse res = Mapper.toProductResponse(productRepo.save(
                Product.builder()
                        .name(body.getName())
                        .images(body.getImages())
                        .description(body.getDescription())
                        .price(body.getPrice())
                        .specialPrice(body.getSpecialPrice())
                        .stockQuantity(body.getStockQuantity())
                        .shop(shop)
                        .category(category)
                        .build()
        ));

        log.info("Add product successfully");
        return res;
    }

    @Override
    @Transactional
    public void updateProduct(ProductRequest body, Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));

        Category category = categoryRepo.findById(body.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + id + " not found"));

        product.setName(body.getName());
        product.setImages(body.getImages());
        product.setDescription(body.getDescription());
        product.setPrice(body.getPrice());
        product.setSpecialPrice(body.getSpecialPrice());
        product.setStockQuantity(body.getStockQuantity());
        product.setCategory(category);

        log.info("Update product successfully");
    }

    @Override
    @Transactional
    public void delProduct(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));

        productRepo.deleteById(id);

        log.info("Delete product successfully");
    }
}
