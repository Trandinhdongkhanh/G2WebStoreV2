package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.OrdersCreationRequest;
import com.hcmute.g2webstorev2.dto.request.RefundReq;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.dto.response.OrdersCreationResponse;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import com.hcmute.g2webstorev2.enums.PaymentType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OrderService {
    OrdersCreationResponse createOrders(OrdersCreationRequest body, HttpServletRequest req, HttpServletResponse res) throws IOException;

    Page<OrderResponse> getMyOrders(OrderStatus orderStatus, int pageNumber, int pageSize);

    OrderResponse sellerUpdateOrderStatus(Integer id, OrderStatus status);

    OrderResponse customerUpdateOrderStatus(Integer id, OrderStatus status);

    void updateUnPaidOrder(String vnp_TxnRef, String zp_trans_id, PaymentType paymentType);

    String payUnPaidOrder(Integer orderId, HttpServletRequest req) throws IOException;

    Page<OrderResponse> getShopOrders(OrderStatus orderStatus, int pageNumber, int pageSize);

    OrderResponse getOrderById(Integer orderId);

    OrderResponse customerRefund(Integer orderId, MultipartFile[] files, RefundReq body);

    Page<OrderResponse> getRefundingOrders(int page, int size);
    Page<OrderResponse> getRefundedOrders(int page, int size);
    OrderResponse refund(Integer orderId);
}
