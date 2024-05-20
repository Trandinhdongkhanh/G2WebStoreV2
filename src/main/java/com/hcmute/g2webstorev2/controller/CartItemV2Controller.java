package com.hcmute.g2webstorev2.controller;


import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemV2Res;
import com.hcmute.g2webstorev2.service.CartItemV2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/cart-item-v2")
@RequiredArgsConstructor
public class CartItemV2Controller {
    private final CartItemV2Service cartItemV2Service;
    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Set<CartItemV2Res>> getCartItems() {
        return ResponseEntity.ok(cartItemV2Service.getCartItems());
    }
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartItemV2Res> addItem(@RequestBody @Valid CartItemRequest body){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartItemV2Service.addItem(body));
    }
}
