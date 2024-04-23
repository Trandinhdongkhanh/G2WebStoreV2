package com.hcmute.g2webstorev2.service;


import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


public interface ProductService {
    Page<ProductResponse> getProductsByName(int pageNumber, int pageSize, String name, Integer seed);

    Page<ProductResponse> getProductByPriceBetween(int pageNumber, int pageSize, Integer startPrice, Integer endPrice, Integer seed);

    Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, Integer seed);

    ProductResponse getProduct(Integer id);

    ProductResponse addProduct(ProductRequest body, MultipartFile[] files);

    void updateProduct(ProductRequest body, Integer id, MultipartFile[] files);

    void delProduct(Integer id);

    Page<ProductResponse> getAllProductsByShop(Integer id, Integer pageNumber, Integer pageSize);

    Page<ProductResponse> getProductsByCategory(Integer id, int pageNumber, int pageSize, Integer seed);
}
