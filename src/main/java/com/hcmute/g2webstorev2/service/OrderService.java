package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.OrdersCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.dto.response.OrdersCreationResponse;
import com.hcmute.g2webstorev2.dto.response.PaymentResponse;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface OrderService {
    OrdersCreationResponse createOrders(OrdersCreationRequest body, HttpServletRequest req, HttpServletResponse res) throws IOException;
    Page<OrderResponse> getMyOrders(OrderStatus orderStatus, int pageNumber, int pageSize);
    OrderResponse sellerUpdateOrderStatus(Integer id, OrderStatus status);
    OrderResponse customerUpdateOrderStatus(Integer id, OrderStatus status);
    void updateUnPaidOrder(String vnp_TxnRef);
    PaymentResponse payUnPaidOrder(Integer orderId, HttpServletRequest req) throws UnsupportedEncodingException;
    Page<OrderResponse> getShopOrders(OrderStatus orderStatus, int pageNumber, int pageSize);

    OrderResponse getOrderById(Integer orderId);
}
