package com.hcmute.g2webstorev2.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderApiRes {
    private Integer code;
    @JsonProperty("code_message_value")
    private String codeMessageValue;
    private CreateOrderResData data;
    private String message;
    @JsonProperty("message_display")
    private String messageDisplay;
}
