package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    @JsonProperty("order_id")
    private Integer orderId;
    @JsonProperty("order_status")
    private OrderStatus orderStatus;
    @JsonProperty("created_date")
    private LocalDateTime createdDate;
    @JsonProperty("cur_date")
    private LocalDateTime curDate;
    @JsonProperty("delivered_date")
    private LocalDateTime deliveredDate;
    @JsonProperty("customer_id")
    private Integer customerId;
    @JsonProperty("shop_id")
    private Integer shopId;
    @JsonProperty("shop_name")
    private String shopName;
    @JsonProperty("products_price_total")
    private Integer productsPriceTotal;
    @JsonProperty("fee_ship")
    private Integer feeShip;
    @JsonProperty("g2_voucher_price_reduce")
    private Integer g2VoucherPriceReduce;
    @JsonProperty("shop_voucher_price_reduce")
    private Integer shopVoucherPriceReduce;
    @JsonProperty("point_spent")
    private Integer pointSpent;
    private Integer total;
    private List<OrderItemResponse> items;
    private AddressResponse address;
}
