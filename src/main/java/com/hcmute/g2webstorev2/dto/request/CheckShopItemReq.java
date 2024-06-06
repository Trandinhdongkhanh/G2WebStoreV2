package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckShopItemReq {
    @JsonProperty("product_id")
    private Integer productId;
    private Integer price;
    private String name;
    private Integer quantity;
}
