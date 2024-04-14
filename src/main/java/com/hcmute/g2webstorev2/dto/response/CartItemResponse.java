package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private String name;
    private Integer price;
    private Integer quantity;
    @JsonProperty("customer_id")
    private Integer customerId;
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("sub_total")
    private Integer subTotal;
}
