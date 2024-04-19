package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import com.hcmute.g2webstorev2.service.OrderService;
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
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> createOrders(@Valid @RequestBody OrderCreationRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrders(body));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> getAllOrdersByMe() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    @PutMapping("/{id}/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> customerUpdateOrderStatus(
            @PathVariable("id") @NotNull(message = "Order ID must not be null")
            @Min(value = 1, message = "Order ID must not be less than 1") Integer id,
            @RequestParam("order_status") OrderStatus status) {
        return ResponseEntity.ok(orderService.customerUpdateOrderStatus(id, status));
    }

    @PutMapping("/{id}/shop")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'SELLER_ORDER_MANAGEMENT') or hasAuthority('UPDATE_ORDER')")
    public ResponseEntity<OrderResponse> sellerUpdateOrderStatus(
            @PathVariable("id") @NotNull(message = "Order ID must not be null")
            @Min(value = 1, message = "Order ID must not be less than 1") Integer id,
            @RequestParam("order_status") OrderStatus status) {
        return ResponseEntity.ok(orderService.sellerUpdateOrderStatus(id, status));
    }
}
