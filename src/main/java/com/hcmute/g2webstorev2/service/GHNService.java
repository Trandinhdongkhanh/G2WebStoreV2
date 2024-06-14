package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.response.ghn.CalculateFeeApiRes;
import com.hcmute.g2webstorev2.entity.Address;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.entity.ShopItem;
import com.hcmute.g2webstorev2.enums.PaymentType;

import java.util.List;

public interface GHNService {
    CalculateFeeApiRes calculateFeeShip(Integer addressId, PaymentType paymentType, Long cartItemId);
}
