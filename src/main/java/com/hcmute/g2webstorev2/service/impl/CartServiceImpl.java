package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import com.hcmute.g2webstorev2.dto.response.CartResponse;
import com.hcmute.g2webstorev2.dto.response.ShopResponse;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.service.CartItemService;
import com.hcmute.g2webstorev2.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemService cartItemService;
    @Override
    public List<CartResponse> getItemsInCart() {
        return null;
    }
}
