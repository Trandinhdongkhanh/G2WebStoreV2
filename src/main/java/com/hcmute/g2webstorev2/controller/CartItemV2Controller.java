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

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart-item-v2")
@RequiredArgsConstructor
public class CartItemV2Controller {
    private final CartItemV2Service cartItemV2Service;
    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<CartItemV2Res>> getCartItems() {
        return ResponseEntity.ok(cartItemV2Service.getCartItems());
    }
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> addItem(@RequestBody @Valid CartItemRequest body){
        cartItemV2Service.addItem(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Cart Item added successfully");
    }
    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> delCartItem(@PathVariable("cartItemId") Long cartItemId){
        cartItemV2Service.delItem(cartItemId);
        return ResponseEntity.ok("Cart item deleted");
    }
    @PutMapping("/{cartItemId}/vouchers/{voucherId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> selectVoucher(
            @PathVariable("cartItemId") Long cartItemId,
            @PathVariable("voucherId") String voucherId
    ){
        cartItemV2Service.selectVoucher(cartItemId, voucherId);
        return ResponseEntity.ok("Select voucher successfully");
    }
    @DeleteMapping("/my-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> dellAllCartItems(){
        cartItemV2Service.delAllItem();
        return ResponseEntity.ok("All items deleted");
    }
}