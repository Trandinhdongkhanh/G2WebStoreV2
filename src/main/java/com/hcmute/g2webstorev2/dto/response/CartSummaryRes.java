package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartSummaryRes {
    @JsonProperty("shop_reduce")
    private Long shopReduce;
    @JsonProperty("fee_ship_reduce")
    private Long feeShipReduce;

}
