package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreationRequest {
    @NotNull(message = "Items cannot be null")
    private List<CartItemResponse> items;
    @JsonProperty("shops_fee_ship")
    private Map<Integer, Integer> shopsFeeShip;
}
