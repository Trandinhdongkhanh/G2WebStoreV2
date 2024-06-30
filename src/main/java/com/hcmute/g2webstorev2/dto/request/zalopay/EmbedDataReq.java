package com.hcmute.g2webstorev2.dto.request.zalopay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmbedDataReq {
    @JsonProperty("preferred_payment_method")
    private List<String> preferredPaymentMethod;
    @JsonProperty("redirecturl")
    private String redirectUrl;
}
