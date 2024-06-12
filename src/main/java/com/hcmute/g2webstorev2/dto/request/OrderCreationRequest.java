package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private List<CheckShopItemReq> items;
    @JsonProperty("fee_ship")
    @NotNull(message = "Fee Ship must not be null")
    private Integer feeShip;
}
