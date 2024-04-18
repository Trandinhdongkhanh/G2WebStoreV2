package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.response.CartResponse;
import com.hcmute.g2webstorev2.entity.CartItem;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.repository.CartItemRepo;
import com.hcmute.g2webstorev2.service.CartItemService;
import com.hcmute.g2webstorev2.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemRepo cartItemRepo;
    @Override
    public List<CartResponse> getItemsInCart() {
        return null;
    }
}
