package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.ghn.ExpectedDeliveryDateReq;
import com.hcmute.g2webstorev2.dto.response.ghn.CalculateFeeApiRes;
import com.hcmute.g2webstorev2.dto.response.ghn.CreateOrderApiRes;
import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.enums.PaymentType;

import java.time.LocalDateTime;


public interface GHNService {
    CalculateFeeApiRes calculateFeeShip(Integer addressId, PaymentType paymentType, Long cartItemId);
    LocalDateTime calculateExceptedDeliveryDate(ExpectedDeliveryDateReq body);
    CreateOrderApiRes createOrder(Order order);
    String printOrder(String ghnOrderCode);
}
