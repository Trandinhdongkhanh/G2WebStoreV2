package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import com.hcmute.g2webstorev2.entity.CartItem;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreationRequest {
    @JsonProperty("order_status")
    private OrderStatus orderStatus;
    @JsonProperty("created_date")
    private LocalDateTime createdDate;
    @JsonProperty("cur_date")
    private LocalDateTime curDate;
    @JsonProperty("delivered_date")
    private LocalDateTime deliveredDate;
    @JsonProperty("shop_id")
    private Integer shopId;
    private List<CartItemResponse> items;
}
