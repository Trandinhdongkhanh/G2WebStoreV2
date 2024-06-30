package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.response.zalopay.CreateOrderRes;
import com.hcmute.g2webstorev2.entity.Order;

import java.util.List;

public interface ZalopayService {
    CreateOrderRes createOrder(long amount, List<Order> orders);
}
