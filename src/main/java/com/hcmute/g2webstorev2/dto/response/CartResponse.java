package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    @JsonProperty("shops_info")
    Map<Integer, ShopResponse> shopsInfo;
    List<CartItemResponse> items;
}
