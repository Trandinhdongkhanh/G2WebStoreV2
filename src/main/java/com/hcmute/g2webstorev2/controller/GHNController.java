package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.ghn.CalculateFeeApiRes;
import com.hcmute.g2webstorev2.enums.PaymentType;
import com.hcmute.g2webstorev2.service.GHNService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ghn")
@RequiredArgsConstructor
public class GHNController {
    private final GHNService ghnService;

    @GetMapping("/calculate-fee-ship")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CalculateFeeApiRes> getFeeShip(
            @RequestParam("addressId") Integer addressId,
            @RequestParam("payment") PaymentType paymentType,
            @RequestParam("cartItemId") Long cartItemId) {
        return ResponseEntity.ok(ghnService.calculateFeeShip(addressId, paymentType, cartItemId));
    }
}
