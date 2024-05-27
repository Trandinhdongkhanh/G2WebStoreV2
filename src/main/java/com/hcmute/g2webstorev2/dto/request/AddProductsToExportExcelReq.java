package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddProductsToExportExcelReq {
    @JsonProperty("product_ids")
    private List<Integer> productIds;
    @JsonProperty("is_all_products")
    private boolean isAllProducts;
}
