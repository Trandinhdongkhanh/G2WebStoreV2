package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.request.OrdersCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    List<OrderResponse> createOrders(OrdersCreationRequest body);
    List<OrderResponse> getMyOrders();
    OrderResponse sellerUpdateOrderStatus(Integer id, OrderStatus status);
    OrderResponse customerUpdateOrderStatus(Integer id, OrderStatus status);
}
