package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.VoucherRequest;
import com.hcmute.g2webstorev2.dto.response.VoucherResponse;

import java.util.List;

public interface VoucherService {
    VoucherResponse addVoucher(VoucherRequest body);
    void updateVoucher(VoucherRequest body ,String id);
    VoucherResponse getVoucher(String id);
    List<VoucherResponse> getAllVouchers();
    List<VoucherResponse> getAllVouchersByProduct(Integer id);
}
