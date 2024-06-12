package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemV2Res;

import java.util.List;

public interface CartItemV2Service {
    void addItem(CartItemRequest body);
    List<CartItemV2Res> getCartItems();
    void selectVoucher(Long cartItemV2Id, String voucherId);
    void delItem(Long cartItemId);
}
