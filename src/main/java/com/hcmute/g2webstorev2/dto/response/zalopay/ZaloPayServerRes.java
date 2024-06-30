package com.hcmute.g2webstorev2.dto.response.zalopay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZaloPayServerRes {
    @JsonProperty("return_code")
    private Integer returnCode;
    @JsonProperty("return_message")
    private String returnMessage;
}
