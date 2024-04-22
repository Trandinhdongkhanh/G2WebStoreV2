package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import com.hcmute.g2webstorev2.service.CartItemService;
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
@RequestMapping("/api/v1/cart-items")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;
    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartItemResponse> addItem(@Valid @RequestBody CartItemRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartItemService.addItem(body));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartItemResponse> updateItem(@Valid @RequestBody CartItemRequest body) {
        return ResponseEntity.ok(cartItemService.updateItem(body));
    }
    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> delItem(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0") Integer id){
        cartItemService.delItem(id);
        return ResponseEntity.ok("Item with ID = " + id + " deleted successfully");
    }
}
