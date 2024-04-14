package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.VoucherRequest;
import com.hcmute.g2webstorev2.dto.response.VoucherResponse;
import com.hcmute.g2webstorev2.service.VoucherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> getVoucher(
            @PathVariable("id")
            @NotBlank(message = "Voucher ID cannot be blank") String id
    ) {
        return ResponseEntity.ok(voucherService.getVoucher(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER_FULL_ACCESS', 'SELLER_PROMOTION_ACCESS') or " +
            "hasAuthority('CREATE_PROMOTION')")
    public ResponseEntity<VoucherResponse> addVoucher(@RequestBody @Valid VoucherRequest body){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(voucherService.addVoucher(body));
    }
}
