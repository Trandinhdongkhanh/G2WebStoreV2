package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemResponse;

import java.util.List;

public interface CartItemService {
    List<CartItemResponse> getAllItems();
    CartItemResponse addItem(CartItemRequest body);
    void delItem(Integer productId);
    CartItemResponse updateItem(CartItemRequest body);
}
