package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdersCreationResponse {
    private List<OrderResponse> orders;
    @JsonProperty("payment_url")
    private String paymentUrl;
}
