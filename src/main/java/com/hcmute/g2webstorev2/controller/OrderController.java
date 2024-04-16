package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> createOrder(@Valid @RequestBody OrderCreationRequest body){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrders(body));
    }
}
