package com.hcmute.g2webstorev2.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductStatisticalRes {
    @JsonProperty("day")
    private DayStatisticalRes dayStatistical;
    @JsonProperty("month")
    private MonthStatisticalRes monthStatisticalRes;
}
