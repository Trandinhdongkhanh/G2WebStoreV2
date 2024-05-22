package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddVoucherToCartItemReq {
    @JsonProperty("voucher_id")
    @NotBlank(message = "Voucher ID must not be blank")
    private String voucherId;
    @JsonProperty("cart_item_v2_id")
    @NotNull(message = "Cart Item ID must not be null")
    @Min(value = 1, message = "Cart Item ID must not be less than 1")
    private Long cartItemV2Id;
}
