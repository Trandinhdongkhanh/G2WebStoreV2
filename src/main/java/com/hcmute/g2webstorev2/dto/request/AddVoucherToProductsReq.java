package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddVoucherToProductsReq {
    @JsonProperty("product_ids")
    private List<Integer> productIds;
}
