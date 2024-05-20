package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.entity.CartItemV2;
import com.hcmute.g2webstorev2.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopItemRes {
    @JsonProperty("shop_item_id")
    private Long shopItemId;
    @JsonProperty("cart_item_v2_id")
    private Long cartItemV2Id;
    private Long price;
    private String name;
    private Integer quantity;
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("subtotal")
    private Long subTotal;
}
