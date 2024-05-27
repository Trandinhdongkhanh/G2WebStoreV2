package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.response.ShopItemRes;
import com.hcmute.g2webstorev2.entity.CartItemV2;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.ShopItem;

public interface ShopItemService {
    ShopItem addItem(CartItemV2 cartItemV2, Product product, Integer quantity);

    void delItem(Long shopItemId);

    ShopItemRes updateQuantity(Long shopItemId, Integer quantity);
}
