package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.ShopRequest;
import com.hcmute.g2webstorev2.dto.response.ShopResponse;
import com.hcmute.g2webstorev2.service.ShopService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/shops")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> getShop(
            @PathVariable("id")
            @NotNull(message = "Shop ID cannot be null")
            @Min(value = 1, message = "Shop ID must be equals or greater than 1") Integer id) {
        return ResponseEntity.ok(shopService.getShopInfo(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ShopResponse>> getShops(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return ResponseEntity.ok(shopService.getAllShops(page, size));
    }

    @PutMapping
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS')")
    public ResponseEntity<ShopResponse> updateShopInfo(@RequestBody @Valid ShopRequest body) {
        return ResponseEntity.ok(shopService.updateShopInfo(body));
    }

    @PutMapping("/{shopId}/locked")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopResponse> lockedShop(@PathVariable("shopId") Integer shopId) {
        return ResponseEntity.ok(shopService.lockShop(shopId));
    }

    @PutMapping("/upload-image")
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS')")
    public ResponseEntity<ShopResponse> uploadShopImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(shopService.uploadShopImage(file));
    }
}