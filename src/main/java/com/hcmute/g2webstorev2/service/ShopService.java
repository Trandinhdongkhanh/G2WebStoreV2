package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.ShopRequest;
import com.hcmute.g2webstorev2.dto.response.ShopResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ShopService {
    ShopResponse getShopInfo(Integer id);
    ShopResponse updateShopInfo(ShopRequest body);
    ShopResponse uploadShopImage(MultipartFile file);
    Page<ShopResponse> getAllShops(int pageNum, int pageSize, boolean isReadyToBanned);
    ShopResponse lockShop(Integer shopId);
}
