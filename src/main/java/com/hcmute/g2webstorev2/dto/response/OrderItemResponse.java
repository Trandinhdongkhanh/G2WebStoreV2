package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    @JsonProperty("item_id")
    private Integer itemId;
    private String image;
    private Integer price;
    private Integer quantity;
    private String name;
    @JsonProperty("order_id")
    private Integer orderId;
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("sub_total")
    private Integer subTotal;
    @JsonProperty("is_reviewed")
    private boolean isReviewed;
}
