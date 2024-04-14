package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.ShopRequest;
import com.hcmute.g2webstorev2.dto.response.ShopResponse;

public interface ShopService {
    ShopResponse getShopInfo(Integer id);
    ShopResponse updateShopInfo(ShopRequest body);
    void delShop(Integer id);
}
