package com.hcmute.g2webstorev2.dto.response;

import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VNPayTransactionRes {
    private String vnp_TmnCode;
    private String vnp_Amount;
    private String vnp_BankCode;
    private String vnp_BankTranNo;
    private String vnp_CardType;
    private String vnp_PayDate;
    private String vnp_OrderInfo;
    private String vnp_TransactionNo;
    private String vnp_ResponseCode;

    //Status code of VNPAY, for more detail visit their website
    private String vnp_TransactionStatus;
    private String vnp_TxnRef;
    private String vnp_SecureHashType;
    private String vnp_SecureHash;
}
