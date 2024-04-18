package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.CartResponse;
import com.hcmute.g2webstorev2.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/intended-cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> getCartInfo(){
        return ResponseEntity.ok(cartService.getCartInfo());
    }
}
