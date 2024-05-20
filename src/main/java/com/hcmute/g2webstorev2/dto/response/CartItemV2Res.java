package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemV2Res {
    @JsonProperty("cart_item_id")
    private Long cartItemId;
    @JsonProperty("customer_id")
    private Integer customerId;
    @JsonProperty("shop_name")
    private String shopName;
    @JsonProperty("shop_id")
    private Integer shopId;
    @JsonProperty("shop_items")
    private Set<ShopItemRes> shopItems;
    private Set<VoucherResponse> vouchers;
    @JsonProperty("shop_subtotal")
    public Long shopSubTotal;
}
