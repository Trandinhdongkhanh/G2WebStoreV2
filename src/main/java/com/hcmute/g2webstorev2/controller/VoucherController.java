package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.AddVoucherToProductsReq;
import com.hcmute.g2webstorev2.dto.request.VoucherRequest;
import com.hcmute.g2webstorev2.dto.response.VoucherResponse;
import com.hcmute.g2webstorev2.enums.VoucherStatus;
import com.hcmute.g2webstorev2.service.VoucherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CollectionTypeRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @PostMapping("/{voucherId}/add-to-products")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'SELLER_PROMOTION_ACCESS') or " +
            "hasAnyAuthority('UPDATE_PROMOTION')")
    public ResponseEntity<String> addVoucherToProducts(
            @PathVariable("voucherId") String voucherId,
            @Valid @RequestBody AddVoucherToProductsReq body
    ) {
        voucherService.addVoucherToProducts(body, voucherId);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/shop")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'SELLER_PROMOTION_ACCESS', 'SELLER_READ_ONLY') or hasAuthority('READ_PROMOTION')")
    public ResponseEntity<Page<VoucherResponse>> getShopVouchers(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "size") int size,
            @RequestParam(defaultValue = "ALL", name = "status") VoucherStatus voucherStatus,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "voucherId") String voucherId
    ) {
        return ResponseEntity.ok(voucherService.getShopVouchers(name, voucherId, voucherStatus, page, size));
    }

    @PutMapping("/{voucherId}/pause")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'SELLER_PROMOTION_ACCESS', 'SELLER_READ_ONLY') or hasAuthority('READ_PROMOTION')")
    public ResponseEntity<VoucherResponse> pauseVoucher(
            @RequestParam("isPaused") boolean isPaused,
            @PathVariable("voucherId") String voucherId
    ) {
        return ResponseEntity.ok(voucherService.pauseVoucher(voucherId, isPaused));
    }
}
