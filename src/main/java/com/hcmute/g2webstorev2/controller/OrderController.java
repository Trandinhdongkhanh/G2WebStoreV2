package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.OrdersCreationRequest;
import com.hcmute.g2webstorev2.dto.request.RefundReq;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.dto.response.OrdersCreationResponse;
import com.hcmute.g2webstorev2.dto.response.vnpay.PaymentResponse;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import com.hcmute.g2webstorev2.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_FULL_ACCESS'," +
            "'SELLER_ORDER_MANAGEMENT'," +
            "'SELLER_READ_ONLY') or hasAuthority('READ_ORDER')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrdersCreationResponse> createOrders(
            @Valid @RequestBody OrdersCreationRequest body,
            HttpServletResponse res,
            HttpServletRequest req) throws IOException {
        return ResponseEntity.ok(orderService.createOrders(body, req, res));
    }

    @PostMapping("/{id}/pay-unpaid-order")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> payUnPaidOrder(
            @PathVariable("id") Integer id,
            HttpServletRequest req) throws UnsupportedEncodingException {
        return ResponseEntity.ok(orderService.payUnPaidOrder(id, req));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderResponse>> getAllOrdersByMe(
            @RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus,
            @RequestParam("page") int pageNumber,
            @RequestParam("size") int pageSize) {
        return ResponseEntity.ok(orderService.getMyOrders(orderStatus, pageNumber, pageSize));
    }

    @PutMapping("/{id}/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> customerUpdateOrderStatus(
            @PathVariable("id") @NotNull(message = "Order ID must not be null")
            @Min(value = 1, message = "Order ID must not be less than 1") Integer id,
            @RequestParam("orderStatus") OrderStatus status) {
        return ResponseEntity.ok(orderService.customerUpdateOrderStatus(id, status));
    }

    @PutMapping("/{id}/shop")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'SELLER_ORDER_MANAGEMENT') or hasAuthority('UPDATE_ORDER')")
    public ResponseEntity<OrderResponse> sellerUpdateOrderStatus(
            @PathVariable("id") @NotNull(message = "Order ID must not be null")
            @Min(value = 1, message = "Order ID must not be less than 1") Integer id,
            @RequestParam("orderStatus") OrderStatus status) {
        return ResponseEntity.ok(orderService.sellerUpdateOrderStatus(id, status));
    }

    @GetMapping("/shop")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'SELLER_ORDER_MANAGEMENT') or hasAuthority('READ_ORDER')")
    public ResponseEntity<Page<OrderResponse>> getShopOrders(
            @RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus,
            @RequestParam("page") int pageNumber,
            @RequestParam("size") int pageSize) {
        return ResponseEntity.ok(orderService.getShopOrders(orderStatus, pageNumber, pageSize));
    }

    @PutMapping("/{orderId}/customer-refund")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> customerRefund(
            @PathVariable("orderId") Integer orderId,
            @RequestParam("files") MultipartFile[] files,
            @Valid @ModelAttribute RefundReq body) {
        return ResponseEntity.ok(orderService.customerRefund(orderId, files, body));
    }

    @GetMapping("/admin/refunding")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> adminGetRefundingOrders(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "size") int size
    ) {
        return ResponseEntity.ok(orderService.getRefundingOrders(page, size));
    }
    @GetMapping("/admin/refunded")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> adminGetRefundedOrders(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "size") int size
    ) {
        return ResponseEntity.ok(orderService.getRefundedOrders(page, size));
    }
}
