package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getOrders();
    List<OrderResponse> getOrdersByCustomer(Integer id);
    List<OrderResponse> createOrders(OrderCreationRequest body);
    List<OrderResponse> getOrdersByShop();
}
