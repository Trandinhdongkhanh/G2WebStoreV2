package com.hcmute.g2webstorev2.dto.request.zalopay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemData {
    @JsonProperty("item_id")
    private Integer itemId;
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("item_price")
    private Long itemPrice;
    @JsonProperty("item_quantity")
    private Integer itemQuantity;
}
