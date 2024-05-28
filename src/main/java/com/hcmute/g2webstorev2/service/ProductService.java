package com.hcmute.g2webstorev2.service;


import com.hcmute.g2webstorev2.dto.request.AddProductsToShopCateRequest;
import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.enums.ShopProductsSortType;
import com.hcmute.g2webstorev2.enums.SortType;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface ProductService {
    void updateProducts(List<Product> products) throws IOException;
    Page<ProductResponse> getProductsByName(String name, int pageNumber, int pageSize, Integer seed,
                                            SortType sortType, Integer startPrice, Integer endPrice, Integer districtId);
    Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, Integer seed, SortType sortType,
                                         Integer startPrice, Integer endPrice, Integer districtId);
    ProductResponse getProduct(Integer id);
    ProductResponse addProduct(ProductRequest body, MultipartFile[] files);
    void updateProduct(ProductRequest body, Integer id, MultipartFile[] files);
    void delProduct(Integer id);
    Page<ProductResponse> getAllProductsByShop(Integer id, Integer pageNumber, Integer pageSize,
                                               ShopProductsSortType sortType);
    Page<ProductResponse> getProductsByCategory(
            Integer id, int pageNumber, int pageSize, Integer seed,
            SortType sortType, Integer startPrice, Integer endPrice, Integer districtId);
    void addProductsToShopCate(Integer shopCateId, AddProductsToShopCateRequest body);
    Page<ProductResponse> getProductsByShopCate(Integer id, int pageNumber, int pageSize);
    List<ProductResponse> getTopFivePopularProductByShop(Integer shopId);

    ProductResponse getProductById(Integer productId);
}
