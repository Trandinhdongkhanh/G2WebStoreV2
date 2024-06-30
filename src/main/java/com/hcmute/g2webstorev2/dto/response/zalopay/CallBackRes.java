package com.hcmute.g2webstorev2.dto.response.zalopay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallBackRes {
    @JsonProperty("data")
    private CallBackData data;
    @JsonProperty("mac")
    private String mac;
    @JsonProperty("type")
    private Integer type;
}
