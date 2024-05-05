package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreationRequest {
    @NotNull(message = "Items cannot be null")
    private List<CartItemResponse> items;
    @JsonProperty("shop_id")
    @NotNull(message = "Shop ID must not be null")
    @Min(value = 1, message = "Shop ID must not be less than 1")
    private Integer shopId;
    @JsonProperty("fee_ship")
    @NotNull(message = "Fee Ship must not be null")
    private Integer feeShip;
}
