package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.ShopCateRequest;
import com.hcmute.g2webstorev2.dto.response.ShopCateResponse;

import java.util.List;

public interface ShopCateService {
    ShopCateResponse getShopCategory(Integer id);
    void updateShopCategory(ShopCateRequest body, Integer id);
    void delShopCategory(Integer id);
    ShopCateResponse addShopCategory(ShopCateRequest body);
    List<ShopCateResponse> getAllShopCategoriesByShopId(Integer id);
}
