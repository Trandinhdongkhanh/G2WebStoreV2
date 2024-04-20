package com.hcmute.g2webstorev2.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NotNull
public class CartResponseV2 {
    private ShopResponse shops;
    private List<CartItemResponse> items;
}
