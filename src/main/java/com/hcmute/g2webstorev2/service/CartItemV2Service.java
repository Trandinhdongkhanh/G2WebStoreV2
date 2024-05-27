package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AddVoucherToCartItemReq;
import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemV2Res;

import java.util.Set;

public interface CartItemV2Service {
    CartItemV2Res addItem(CartItemRequest body);
    Set<CartItemV2Res> getCartItems();
    CartItemV2Res addVoucher(AddVoucherToCartItemReq body);
    void delItem(Long cartItemId);
}
