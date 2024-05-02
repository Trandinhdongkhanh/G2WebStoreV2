package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.OrdersCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.dto.response.PaymentResponse;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import com.hcmute.g2webstorev2.service.OrderService;
import com.hcmute.g2webstorev2.service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private VNPAYService vnpayService;

    @PostMapping("/create-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> createOrders(
            @Valid @RequestBody OrdersCreationRequest body,
            HttpServletResponse res,
            HttpServletRequest req) throws IOException {
        List<OrderResponse> orderResponses = orderService.createOrders(body, req, res);

        return ResponseEntity.ok(orderResponses);
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
