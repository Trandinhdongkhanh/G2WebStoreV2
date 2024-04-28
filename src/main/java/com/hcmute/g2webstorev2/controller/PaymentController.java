package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.*;
import com.hcmute.g2webstorev2.service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestParam("amount") int reqAmount,
            @RequestParam(value = "bankCode", required = false) String bankCode,
            @RequestParam(value = "language", required = false) String language,
            HttpServletRequest req
    ) throws IOException {
        return ResponseEntity.ok(vnpayService.createPayment(reqAmount, bankCode, language, req));
    }

    @GetMapping("/query-payment")
    public ResponseEntity<MerchantResponseToVNPAY> IPNPaymentInfo(
            @RequestParam("order_id") String orderId,
            @RequestParam("trans_date") String transDate,
            HttpServletRequest req
    ) throws IOException {
        IPNResponse res = vnpayService.queryPayment(orderId, transDate, req);
        return ResponseEntity.ok(MerchantResponseToVNPAY.builder()
                        .data(res)
                        .Message("Giao dịch thành công")
                        .RspCode("00")
                .build());
    }

    @GetMapping("/callback-url")
    public ResponseEntity<TransactionResponse> paymentInfo(
            @RequestParam("vnp_TxnRef") String vnp_TxnRef,
            @RequestParam("vnp_Amount") String vnp_Amount,
            @RequestParam("vnp_OrderInfo") String vnp_OrderInfo,
            @RequestParam("vnp_ResponseCode") String vnp_ResponseCode,
            @RequestParam("vnp_TransactionNo") String vnp_TransactionNo,
            @RequestParam("vnp_BankCode") String vnp_BankCode,
            @RequestParam("vnp_PayDate") String vnp_PayDate,

            //Status code of VNPAY, for more detail visit their website
            @RequestParam("vnp_TransactionStatus") String vnp_TransactionStatus
    ) {
        if (vnp_TransactionStatus.equals("00"))
            return ResponseEntity.ok(TransactionResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("Successfully")
                    .data(VNPAYTransactionRes.builder()
                            .vnp_TxnRef(vnp_TxnRef)
                            .vnp_Amount(vnp_Amount)
                            .vnp_OrderInfo(vnp_OrderInfo)
                            .vnp_ResponseCode(vnp_ResponseCode)
                            .vnp_TransactionNo(vnp_TransactionNo)
                            .vnp_BankCode(vnp_BankCode)
                            .vnp_PayDate(vnp_PayDate)
                            .vnp_TransactionStatus(vnp_TransactionStatus)
                            .build())
                    .build());

        return ResponseEntity.badRequest().body(TransactionResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .message("Failed")
                .build());
    }
}
