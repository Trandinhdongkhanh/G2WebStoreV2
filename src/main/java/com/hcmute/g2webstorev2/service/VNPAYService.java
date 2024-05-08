package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.response.PaymentResponse;
import com.hcmute.g2webstorev2.dto.response.VNPayTransactionQueryRes;
import com.hcmute.g2webstorev2.dto.response.VNPayTransactionRefundRes;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface VNPAYService {
    PaymentResponse createPayment(int reqAmount, String bankCode, String language, HttpServletRequest req) throws UnsupportedEncodingException;

    VNPayTransactionQueryRes getTransactionInfoFromVNPay(String orderId, String transDate, HttpServletRequest req) throws IOException;

    boolean isValidSignValue(String vnp_SecureHash, HttpServletRequest req);

    VNPayTransactionRefundRes refund(int reqAmount, String vnp_TxnRef, HttpServletRequest req,
                String vnp_TransactionType, String vnp_TransactionDate, String vnp_CreateBy);
}
