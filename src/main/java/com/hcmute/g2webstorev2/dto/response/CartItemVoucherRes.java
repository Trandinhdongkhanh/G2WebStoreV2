package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemVoucherRes {
    @JsonProperty("voucher_id")
    private String voucherId;
    @JsonProperty("cart_item_v2_id")
    private Long cartItemV2Id;
    @JsonProperty("is_selected")
    private Boolean isSelected;
    @JsonProperty("is_eligible")
    private Boolean isEligible;
}
