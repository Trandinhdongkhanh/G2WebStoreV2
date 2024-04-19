package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.VoucherRequest;
import com.hcmute.g2webstorev2.dto.response.VoucherResponse;
import com.hcmute.g2webstorev2.service.VoucherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> getVoucher(
            @PathVariable("id")
            @NotBlank(message = "Voucher ID cannot be blank") String id) {
        return ResponseEntity.ok(voucherService.getVoucher(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER_FULL_ACCESS', 'SELLER_PROMOTION_ACCESS') or " +
            "hasAuthority('CREATE_PROMOTION')")
    public ResponseEntity<VoucherResponse> addVoucher(@RequestBody @Valid VoucherRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(voucherService.addVoucher(body));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<List<VoucherResponse>> getVouchersFromProduct(
            @PathVariable("id") @NotNull(message = "Product ID must not be null")
            @Min(value = 1, message = "Product ID must not be less than 1") Integer id) {
        return ResponseEntity.ok(voucherService.getVouchersByProduct(id));
    }
}
