package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.CartItemV2;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShopItemRepo extends JpaRepository<ShopItem, Long> {
    Optional<ShopItem> findByCartItemV2AndProduct(CartItemV2 cartItemV2, Product product);
    int deleteAllByProduct(Product product);
    @Query("delete from ShopItem si where si.shopItemId =:shopItemId")
    int deleteByShopItemId(Integer shopItemId);
}
