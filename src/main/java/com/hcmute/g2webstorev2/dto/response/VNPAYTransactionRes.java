package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VNPAYTransactionRes {
    private String vnp_TxnRef;
    private String vnp_Amount;
    private String vnp_OrderInfo;
    private String vnp_ResponseCode;
    private String vnp_TransactionNo;
    private String vnp_BankCode;
    private String vnp_PayDate;
    private String vnp_TransactionStatus;
}
