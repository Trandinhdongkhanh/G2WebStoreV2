package com.hcmute.g2webstorev2.dto.response.zalopay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRes {
    @JsonProperty("return_code")
    private Integer returnCode;
    @JsonProperty("return_message")
    private String returnMessage;
    @JsonProperty("sub_return_code")
    private Integer subReturnCode;
    @JsonProperty("sub_return_message")
    private String subReturnMessage;
    @JsonProperty("order_url")
    private String orderUrl;
    @JsonProperty("zp_trans_token")
    private String zpTransToken;
    @JsonProperty("order_token")
    private String orderToken;
    @JsonProperty("qr_code")
    private String qrCode;
}
