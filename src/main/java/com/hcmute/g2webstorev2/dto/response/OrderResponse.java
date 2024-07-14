package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import com.hcmute.g2webstorev2.enums.PaymentType;
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
    @JsonProperty("expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;
    @JsonProperty("created_date")
    private LocalDateTime createdDate;
    @JsonProperty("payed_date")
    private LocalDateTime payedDate;
    @JsonProperty("delivered_date")

    private LocalDateTime deliveredDate;
    @JsonProperty("ghn_order_code")
    private String ghnOrderCode;
    @JsonProperty("customer_id")
    private Integer customerId;
    @JsonProperty("shop_id")
    private Integer shopId;
    @JsonProperty("shop_name")
    private String shopName;
    @JsonProperty("fee_ship")
    private Integer feeShip;
    @JsonProperty("g2_voucher_price_reduce")
    private Integer g2VoucherPriceReduce;
    @JsonProperty("shop_voucher_price_reduce")
    private Integer shopVoucherPriceReduce;
    @JsonProperty("fee_ship_reduce")
    private Integer feeShipReduce;
    @JsonProperty("point_spent")
    private Double pointSpent;
    @JsonProperty("grand_total")
    private Integer grandTotal;
    @JsonProperty("shop_total")
    private Integer shopTotal;
    private List<OrderItemResponse> items;
    @JsonProperty("payment_type")
    private PaymentType paymentType;
    private AddressResponse address;
    @JsonProperty("refund_reason")
    private String refundReason;
    @JsonProperty("refunded_at")
    private LocalDateTime refundedAt;
    @JsonProperty("refunding_at")
    private LocalDateTime refundingAt;
    @JsonProperty("refund_images")
    private List<GCPFileResponse> refundImages;
}
