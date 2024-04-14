package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import com.hcmute.g2webstorev2.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart-items")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;
    @GetMapping("/me")
    public ResponseEntity<List<CartItemResponse>> getAllItems(){
        return ResponseEntity.ok(cartItemService.getAllItems());
    }
}
