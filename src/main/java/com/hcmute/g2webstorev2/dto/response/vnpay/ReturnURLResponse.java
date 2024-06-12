package com.hcmute.g2webstorev2.dto.response.vnpay;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnURLResponse {
    private String vnp_Rsp;
    private String message;
    private VNPayTransactionRes data;
}
