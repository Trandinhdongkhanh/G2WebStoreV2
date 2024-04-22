package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.response.CartResponse;
import com.hcmute.g2webstorev2.entity.CartItem;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemRepo;
import com.hcmute.g2webstorev2.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemRepo cartItemRepo;
    @Override
    public List<CartResponse> getCartInfo() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<Shop, List<CartItem>> itemsInShop = new LinkedHashMap<>();
        List<CartResponse> res = new ArrayList<>();

        cartItemRepo.findAllByCustomer(customer).forEach(item -> {
            Shop shop = item.getProduct().getShop();
            item.setSubTotal(item.getProduct().getPrice() * item.getQuantity());
            if (!itemsInShop.containsKey(shop)) {
                itemsInShop.put(shop, new ArrayList<>(List.of(item)));
                return;
            }
            itemsInShop.get(shop).add(item);
        });

        for (var entry : itemsInShop.entrySet()){
            int total = 0;
            Shop shop = entry.getKey();
            List<CartItem> items = entry.getValue();

            for (CartItem item : items) total += item.getSubTotal();
            res.add(CartResponse.builder()
                    .shop(Mapper.toShopResponse(shop))
                    .items(items.stream()
                            .map(Mapper::toCartItemResponse)
                            .collect(Collectors.toList()))
                    .total(total)
                    .build());
        }
        return res;
    }
}
