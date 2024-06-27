package com.hcmute.g2webstorev2.dto.request.vnpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VNPayTransRefundReq {
    @JsonProperty("vnp_RequestId")
    private String vnpRequestId;
    @JsonProperty("vnp_Version")
    private String vnpVersion;
    @JsonProperty("vnp_Command")
    private String vnpCommand;
    @JsonProperty("vnp_TmnCode")
    private String vnpTmnCode;
    @JsonProperty("vnp_TransactionType")
    private String vnpTransactionType;
    @JsonProperty("vnp_TxnRef")
    private String vnpTxnRef;
    @JsonProperty("vnp_Amount")
    private String vnpAmount;
    @JsonProperty("vnp_OrderInfo")
    private String vnpOrderInfo;
    @JsonProperty("vnp_TransactionNo")
    private String vnpTransactionNo;
    @JsonProperty("vnp_TransactionDate")
    private String vnpTransactionDate;
    @JsonProperty("vnp_CreateBy")
    private String vnpCreateBy;
    @JsonProperty("vnp_CreateDate")
    private String vnpCreateDate;
    @JsonProperty("vnp_IpAddr")
    private String vnpIpAddr;
    @JsonProperty("vnp_SecureHash")
    private String vnpSecureHash;
}
