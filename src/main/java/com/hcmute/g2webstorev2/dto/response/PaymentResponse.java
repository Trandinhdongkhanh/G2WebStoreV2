package com.hcmute.g2webstorev2.dto.response;

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
    private String data;
}
