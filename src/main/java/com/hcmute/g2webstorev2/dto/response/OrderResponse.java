package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
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
}
