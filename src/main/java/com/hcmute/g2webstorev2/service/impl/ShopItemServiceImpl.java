package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.response.ShopItemRes;
import com.hcmute.g2webstorev2.entity.CartItemV2;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.ShopItem;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemV2Repo;
import com.hcmute.g2webstorev2.repository.ShopItemRepo;
import com.hcmute.g2webstorev2.service.ShopItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopItemServiceImpl implements ShopItemService {
    private final ShopItemRepo shopItemRepo;
    private final CartItemV2Repo cartItemV2Repo;
    @Override
    @Transactional
    public void delItem(Long shopItemId) {
        ShopItem shopItem = shopItemRepo.findById(shopItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));

        CartItemV2 cartItemV2 = shopItem.getCartItemV2();
        if (cartItemV2.getShopItems().size() == 1) {
            cartItemV2.setShopItems(null);
            shopItem.setCartItemV2(null);
            cartItemV2Repo.delete(cartItemV2);
            return;
        }
        shopItemRepo.delete(shopItem);
        log.info("Shop item with ID = " + shopItem + " deleted");
    }

    @Override
    @Transactional
    public ShopItemRes updateQuantity(Long shopItemId, Integer quantity) {
        ShopItem shopItem = shopItemRepo.findById(shopItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));

        if (quantity == 0) {
            shopItemRepo.delete(shopItem);
            return null;
        }
        shopItem.setQuantity(quantity);
        return Mapper.toShopItemRes(shopItem);
    }
}
