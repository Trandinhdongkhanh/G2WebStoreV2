package com.hcmute.g2webstorev2.service;


import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Page<ProductResponse> getAllProducts(int pageNumber, int pageSize);

    ProductResponse getProduct(Integer id);

    ProductResponse addProduct(ProductRequest body);

    void updateProduct(ProductRequest body, Integer id);

    void delProduct(Integer id);
    List<ProductResponse> getAllProductsByShop(Integer id);
    Page<ProductResponse> getAllRandomProducts(Integer pageNumber);
}
