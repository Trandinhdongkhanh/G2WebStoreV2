package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerMonthRes {
    @JsonProperty("january_count")
    private Long januaryCount;
    @JsonProperty("february_count")
    private Long februaryCount;
    @JsonProperty("march_count")
    private Long marchCount;
    @JsonProperty("april_count")
    private Long aprilCount;
    @JsonProperty("may_count")
    private Long mayCount;
    @JsonProperty("june_count")
    private Long juneCount;
    @JsonProperty("july_count")
    private Long julyCount;
    @JsonProperty("august_count")
    private Long augustCount;
    @JsonProperty("september_count")
    private Long septemberCount;
    @JsonProperty("october_count")
    private Long octoberCount;
    @JsonProperty("november_count")
    private Long novemberCount;
    @JsonProperty("december_count")
    private Long decemberCount;
}
