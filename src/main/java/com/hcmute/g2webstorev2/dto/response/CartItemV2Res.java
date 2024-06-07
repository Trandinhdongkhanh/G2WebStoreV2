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
    private ShopResponse shop;
    @JsonProperty("shop_items")
    private Set<ShopItemRes> shopItems;
    private Set<VoucherResponse> vouchers;
    @JsonProperty("shop_subtotal")
    public Long shopSubTotal;
    @JsonProperty("shop_voucher_reduce")
    public Long shopVoucherReduce;
    @JsonProperty("shop_free_ship_reduce")
    public Long shopFreeShipReduce;
}
