package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.vnpay.*;
import com.hcmute.g2webstorev2.enums.PaymentType;
import com.hcmute.g2webstorev2.service.OrderService;
import com.hcmute.g2webstorev2.service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final VNPAYService vnpayService;
    private final OrderService orderService;
    @GetMapping("/create-payment")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestParam("amount") int reqAmount,
            @RequestParam(value = "bankCode", required = false) String bankCode,
            @RequestParam(value = "language", required = false) String language,
            HttpServletRequest req
    ) throws IOException {
        return ResponseEntity.ok(vnpayService.createPayment(reqAmount, bankCode, language, req));
    }

    @GetMapping("/refund")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<VNPayTransactionRefundRes> refund(
            @RequestParam("vnp_CreateBy") String vnp_CreateBy,
            @RequestParam("vnp_TransactionDate") String vnp_TransactionDate,
            @RequestParam("vnp_TxnRef") String vnp_TxnRef,
//        02: Giao dịch hoàn trả toàn phần (vnp_TransactionType=02)
//        03: Giao dịch hoàn trả một phần (vnp_TransactionType=03)
            @RequestParam("vnp_TransactionType") String vnp_TransactionType,
            @RequestParam("reqAmount") long reqAmount,
            HttpServletRequest req
    ) {
        return ResponseEntity.ok(vnpayService.refund(
                reqAmount, vnp_TxnRef, req, vnp_TransactionType, vnp_TransactionDate, vnp_CreateBy));
    }

    @GetMapping("/query-transaction-from-vnpay")
    @PreAuthorize("hasAnyRole('ADMIN')")
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
            HttpServletRequest req,
            HttpServletResponse res
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
                orderService.updateUnPaidOrder(vnp_TxnRef, PaymentType.VNPAY);
                res.sendRedirect("http://localhost:8002/thanks");
                return ResponseEntity.ok(ReturnURLResponse.builder()
                        .vnp_Rsp(vnp_ResponseCode)
                        .message("Success")
                        .data(vnpayTransactionRes)
                        .build());
            }
        }
        res.sendRedirect("http://localhost:8002");
        return ResponseEntity.badRequest().body(ReturnURLResponse.builder()
                .message("Failed")
                .vnp_Rsp(vnp_ResponseCode)
                .data(vnpayTransactionRes)
                .build());
    }
}
