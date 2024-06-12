package com.hcmute.g2webstorev2.dto.response.vnpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private int code;
    private HttpStatus status;
    private String message;
    @JsonProperty("payment_url")
    private String paymentUrl;
    private String vnp_TxnRef;
    private String vnp_CreateDate;
}
