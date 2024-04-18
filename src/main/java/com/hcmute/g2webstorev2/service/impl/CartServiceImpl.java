package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.response.CartResponse;
import com.hcmute.g2webstorev2.dto.response.ShopResponse;
import com.hcmute.g2webstorev2.entity.CartItem;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemRepo;
import com.hcmute.g2webstorev2.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemRepo cartItemRepo;

    @Override
    public CartResponse getCartInfo() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<Integer, ShopResponse> shopMap = new HashMap<>();

        List<CartItem> cartItems = cartItemRepo.findAllByCustomer(customer);

        cartItems.forEach(item -> {
            Integer shopId = item.getProduct().getShop().getShopId();
            if (shopMap.get(shopId) == null) {
                shopMap.put(shopId, Mapper.toShopResponse(item.getProduct().getShop()));
            }
            item.setSubTotal(item.getProduct().getPrice() * item.getQuantity());
        });

        return CartResponse.builder()
                .shopsInfo(shopMap)
                .items(cartItems.stream().map(Mapper::toCartItemResponse).collect(Collectors.toList()))
                .build();
    }
}
