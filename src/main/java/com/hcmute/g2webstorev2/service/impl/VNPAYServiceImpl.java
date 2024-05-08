package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.config.VNPAYConfig;
import com.hcmute.g2webstorev2.dto.request.VNPayTransRefundReq;
import com.hcmute.g2webstorev2.dto.request.VNPayTransactionQueryRequest;
import com.hcmute.g2webstorev2.dto.response.PaymentResponse;
import com.hcmute.g2webstorev2.dto.response.VNPayTransactionQueryRes;
import com.hcmute.g2webstorev2.dto.response.VNPayTransactionRefundRes;
import com.hcmute.g2webstorev2.service.VNPAYService;
import com.hcmute.g2webstorev2.util.VNPAYUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPAYServiceImpl implements VNPAYService {
    private final VNPAYUtil vnpayUtil;
    private final VNPAYConfig vnpayConfig;

    private Map<String, String> getVNPayParams(long amount, String bankCode, String vnp_TxnRef, String vnp_IpAddr,
                                               String vnp_Command, String locate) {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnpayConfig.getVnp_Version());
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnpayConfig.getVnp_TmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");

        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        String createdDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String expiresDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getVnp_ReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", createdDate);
        vnp_Params.put("vnp_ExpireDate", expiresDate);

        return vnp_Params;
    }

    @Override
    public PaymentResponse createPayment(int reqAmount, String bankCode, String language, HttpServletRequest req) throws UnsupportedEncodingException {
        Map<String, String> vnp_Params = getVNPayParams(
                reqAmount * 100L,
                bankCode,
                vnpayUtil.getRandomNumber(8),
                vnpayUtil.getIpAddress(req),
                "pay",
                language);

        String paymentUrl = vnpayUtil.getPaymentURL(vnp_Params);

        return PaymentResponse.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("success")
                .paymentUrl(paymentUrl)
                .vnp_TxnRef(vnp_Params.get("vnp_TxnRef"))
                .vnp_CreateDate(vnp_Params.get("vnp_CreateDate"))
                .build();
    }

    @Override
    public VNPayTransactionQueryRes getTransactionInfoFromVNPay(String orderId, String transDate, HttpServletRequest req) throws IOException {
        String vnp_RequestId = vnpayUtil.getRandomNumber(8);
        String vnp_Command = "querydr";
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId: " + orderId;
        String vnp_IpAddr = vnpayUtil.getIpAddress(req);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String hash_Data = String.join("|", vnp_RequestId, vnpayConfig.getVnp_Version(), vnp_Command,
                vnpayConfig.getVnp_TmnCode(), orderId, transDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        VNPayTransactionQueryRequest queryReq = VNPayTransactionQueryRequest.builder()
                .vnp_RequestId(vnp_RequestId)
                .vnp_Version(vnpayConfig.getVnp_Version())
                .vnp_Command(vnp_Command)
                .vnp_TmnCode(vnpayConfig.getVnp_TmnCode())
                .vnp_TxnRef(orderId)
                .vnp_OrderInfo(vnp_OrderInfo)
                .vnp_TransactionDate(transDate)
                .vnp_CreateDate(vnp_CreateDate)
                .vnp_IpAddr(vnp_IpAddr)
                .vnp_SecureHash(vnpayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hash_Data))
                .build();

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(vnpayConfig.getVnp_ApiUrl(), queryReq, VNPayTransactionQueryRes.class);
    }

    @Override
    public boolean isValidSignValue(String vnp_SecureHash, HttpServletRequest req) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII);
            String fieldValue = URLEncoder.encode(req.getParameter(fieldName), StandardCharsets.US_ASCII);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        String signValue = vnpayUtil.hashAllFields(fields);
        return signValue.equals(vnp_SecureHash);
    }

    @Override
    public VNPayTransactionRefundRes refund(int reqAmount, String vnp_TxnRef, HttpServletRequest req,
                                            String vnp_TransactionType, String vnp_TransactionDate, String vnp_CreateBy) {
        //Command: refund
        String vnp_RequestId = vnpayUtil.getRandomNumber(8);
        String vnp_Version = vnpayConfig.getVnp_Version();
        String vnp_Command = "refund";
        String vnp_TmnCode = vnpayConfig.getVnp_TmnCode();
        long amount = reqAmount * 100L;
        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = ""; //Assuming value of the parameter "vnp_TransactionNo" does not exist on your system.

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        String vnp_IpAddr = vnpayUtil.getIpAddress(req);

        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        String vnp_SecureHash = vnpayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hash_Data);

        VNPayTransRefundReq refundReq = VNPayTransRefundReq.builder()
                .vnp_RequestId(vnp_RequestId)
                .vnp_Version(vnp_Version)
                .vnp_Command(vnp_Command)
                .vnp_TmnCode(vnp_TmnCode)
                .vnp_TransactionType(vnp_TransactionType)
                .vnp_TxnRef(vnp_TxnRef)
                .vnp_Amount(vnp_Amount)
                .vnp_OrderInfo(vnp_OrderInfo)
                .vnp_TransactionDate(vnp_TransactionDate)
                .vnp_CreateBy(vnp_CreateBy)
                .vnp_CreateDate(vnp_CreateDate)
                .vnp_IpAddr(vnp_IpAddr)
                .vnp_SecureHash(vnp_SecureHash)
                .build();

        if (vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty()) {
            refundReq.setVnp_TransactionNo("{get value of vnp_TransactionNo}");
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(vnpayConfig.getVnp_ApiUrl(), refundReq, VNPayTransactionRefundRes.class);
    }
}
