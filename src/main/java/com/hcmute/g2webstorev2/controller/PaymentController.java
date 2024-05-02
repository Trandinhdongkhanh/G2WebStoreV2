package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.*;
import com.hcmute.g2webstorev2.entity.VNPAYTransaction;
import com.hcmute.g2webstorev2.service.OrderService;
import com.hcmute.g2webstorev2.service.VNPAYService;
import com.hcmute.g2webstorev2.util.VNPAYUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    @Autowired
    private VNPAYService vnpayService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private VNPAYUtil vnpayUtil;

    @GetMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestParam("amount") int reqAmount,
            @RequestParam(value = "bankCode", required = false) String bankCode,
            @RequestParam(value = "language", required = false) String language,
            HttpServletRequest req
    ) throws IOException {
        return ResponseEntity.ok(vnpayService.createPayment(reqAmount, bankCode, language, req));
    }

    @GetMapping("/query-transaction-from-vnpay")
    public ResponseEntity<VNPayTransactionQueryRes> getTransactionInfoFromVNPay(
            @RequestParam("order_id") String orderId,
            @RequestParam("trans_date") String transDate,
            HttpServletRequest req
    ) throws IOException {
        //Can only request after 5 min
        return ResponseEntity.ok(vnpayService.getTransactionInfoFromVNPay(orderId, transDate, req));
    }

    @GetMapping("/return-url")
    public ResponseEntity<ReturnURLResponse> returnUrl(
            @RequestParam("vnp_TmnCode") String vnp_TmnCode,
            @RequestParam("vnp_Amount") String vnp_Amount,
            @RequestParam("vnp_BankCode") String vnp_BankCode,
            @RequestParam(value = "vnp_BankTranNo", required = false) String vnp_BankTranNo,
            @RequestParam(value = "vnp_CardType", required = false) String vnp_CardType,
            @RequestParam(value = "vnp_PayDate", required = false) String vnp_PayDate,
            @RequestParam("vnp_OrderInfo") String vnp_OrderInfo,
            @RequestParam("vnp_TransactionNo") String vnp_TransactionNo,
            @RequestParam("vnp_ResponseCode") String vnp_ResponseCode,

            //Status code of VNPAY, for more detail visit their website
            @RequestParam("vnp_TransactionStatus") String vnp_TransactionStatus,
            @RequestParam("vnp_TxnRef") String vnp_TxnRef,
            @RequestParam(value = "vnp_SecureHashType", required = false) String vnp_SecureHashType,
            @RequestParam("vnp_SecureHash") String vnp_SecureHash,
            HttpServletResponse res,
            HttpServletRequest req
    ) throws IOException {
        VNPayTransactionRes vnpayTransactionRes = VNPayTransactionRes.builder()
                .vnp_TmnCode(vnp_TmnCode)
                .vnp_Amount(vnp_Amount)
                .vnp_BankCode(vnp_BankCode)
                .vnp_BankTranNo(vnp_BankTranNo)
                .vnp_CardType(vnp_CardType)
                .vnp_PayDate(vnp_PayDate)
                .vnp_OrderInfo(vnp_OrderInfo)
                .vnp_TransactionNo(vnp_TransactionNo)
                .vnp_ResponseCode(vnp_ResponseCode)
                .vnp_TransactionStatus(vnp_TransactionStatus)
                .vnp_TxnRef(vnp_TxnRef)
                .vnp_SecureHashType(vnp_SecureHashType)
                .vnp_SecureHash(vnp_SecureHash)
                .build();
        if (vnpayService.isValidSignValue(vnp_SecureHash, req)) {
            if ("00".equals(vnp_TransactionStatus)) {
                orderService.updateUnPaidOrder(vnp_TxnRef);
//                res.sendRedirect("http://localhost:8002/thanks");
                return ResponseEntity.ok(ReturnURLResponse.builder()
                        .vnp_Rsp(vnp_ResponseCode)
                        .message("Success")
                        .data(vnpayTransactionRes)
                        .build());
            }
        }
        return ResponseEntity.badRequest().body(ReturnURLResponse.builder()
                .message("Failed")
                .vnp_Rsp(vnp_ResponseCode)
                .data(vnpayTransactionRes)
                .build());
    }
}
