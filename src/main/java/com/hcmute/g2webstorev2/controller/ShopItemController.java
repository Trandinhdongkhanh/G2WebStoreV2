package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.ShopItemRes;
import com.hcmute.g2webstorev2.service.ShopItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shop-items")
@RequiredArgsConstructor
public class ShopItemController {
    private final ShopItemService shopItemService;

    @PutMapping("/{shopItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ShopItemRes> updateShopItem(
            @PathVariable("shopItemId") Long shopItemId,
            @RequestParam("quantity") Integer quantity
    ) {
        return ResponseEntity.ok(shopItemService.updateQuantity(shopItemId, quantity));
    }

    @DeleteMapping("/{shopItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> delShopItem(@PathVariable("shopItemId") Long shopItemId) {
        shopItemService.delItem(shopItemId);
        return ResponseEntity.ok("Shop item deleted");
    }
}
