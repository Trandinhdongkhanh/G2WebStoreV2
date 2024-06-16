package com.hcmute.g2webstorev2.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpectedDeliverDateData {
    @JsonProperty("lead_time")
    private Long leadTime;
    @JsonProperty("order_date")
    private Long orderDate;
}
