package com.hcmute.g2webstorev2.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private int code;
    private HttpStatus status;
    private String message;
    private VNPAYTransactionRes data;
}
