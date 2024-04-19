package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddVoucherToProductRequest {
    @JsonProperty("voucher_id")
    private String voucherId;
    @JsonProperty("product_ids")
    private List<Integer> productIds;
}
