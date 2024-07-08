package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationReq {
    private String content;
    @JsonProperty("seller_id")
    private Integer sellerId;
    @JsonProperty("customer_id")
    private Integer customerId;
}
