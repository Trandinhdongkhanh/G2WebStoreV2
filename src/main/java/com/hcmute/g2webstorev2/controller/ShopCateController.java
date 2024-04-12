package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.ShopCateRequest;
import com.hcmute.g2webstorev2.dto.response.ShopCateResponse;
import com.hcmute.g2webstorev2.service.ShopCateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shop-categories")
public class ShopCateController {
    @Autowired
    private ShopCateService shopCateService;

    @GetMapping
    public ResponseEntity<List<ShopCateResponse>> getAllShopCategories() {
        return ResponseEntity.ok(shopCateService.getAllShopCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopCateResponse> getShopCategory(
            @PathVariable("id")
            @NotNull(message = "Category ID cannot be null")
            @Min(value = 1, message = "Category ID must be greater than 0") Integer id
    ) {
        return ResponseEntity.ok(shopCateService.getShopCategory(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS') and hasAuthority('CREATE_CATEGORY')")
    public ResponseEntity<ShopCateResponse> addShopCategory(@Valid @RequestBody ShopCateRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(shopCateService.addShopCategory(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS') and hasAuthority('UPDATE_CATEGORY')")
    public ResponseEntity<String> updateShopCategory(
            @Valid @RequestBody ShopCateRequest body,
            @PathVariable("id")
            @NotNull(message = "Category ID cannot be null")
            @Min(value = 1, message = "Category ID must be greater than 0") Integer id
    ) {
        shopCateService.updateShopCategory(body, id);
        return ResponseEntity.ok("Category with ID = " + id + " updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS') and hasAuthority('DELETE_CATEGORY')")
    public ResponseEntity<String> delShopCategory(
            @PathVariable("id")
            @NotNull(message = "Category ID cannot be null")
            @Min(value = 1, message = "Category ID must be greater than 0") Integer id
    ) {
        shopCateService.delShopCategory(id);
        return ResponseEntity.ok("Category with ID = " + id + " deleted successfully");
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ShopCateResponse>> getAllShopCategoriesByShop(
            @PathVariable("shopId")
            @NotNull(message = "Shop ID cannot be null")
            @Min(value = 1, message = "Shop ID must be greater than 0") Integer id
    ) {
        return ResponseEntity.ok(shopCateService.getAllShopCategoriesByShopId(id));
    }

}
