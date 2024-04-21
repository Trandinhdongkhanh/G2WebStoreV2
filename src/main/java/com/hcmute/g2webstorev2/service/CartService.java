package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.response.CartResponse;

import java.util.List;

public interface CartService {
    List<CartResponse> getCartInfo();
}
