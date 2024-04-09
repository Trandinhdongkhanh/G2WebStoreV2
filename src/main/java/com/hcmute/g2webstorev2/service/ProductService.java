package com.hcmute.g2webstorev2.service;


import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();

    ProductResponse getProduct(Integer id);

    ProductResponse addProduct(ProductRequest body);

    void updateProduct(ProductRequest body, Integer id);

    void delProduct(Integer id);
}
