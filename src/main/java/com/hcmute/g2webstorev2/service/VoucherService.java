package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AddVoucherToProductsReq;
import com.hcmute.g2webstorev2.dto.request.VoucherRequest;
import com.hcmute.g2webstorev2.dto.response.VoucherResponse;
import com.hcmute.g2webstorev2.enums.VoucherStatus;
import org.springframework.data.domain.Page;

import java.util.List;


public interface VoucherService {
    VoucherResponse addVoucher(VoucherRequest body);

    VoucherResponse getVoucher(String id);

    List<VoucherResponse> getVouchersByProduct(Integer id);

    void addVoucherToProducts(AddVoucherToProductsReq body, String voucherId);

    Page<VoucherResponse> getShopVouchers(String name, String voucherId, VoucherStatus status, int page, int size);

    VoucherResponse pauseVoucher(String voucherId, boolean isPaused);
}
