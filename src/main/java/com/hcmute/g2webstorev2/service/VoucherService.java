package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AddVoucherToProductRequest;
import com.hcmute.g2webstorev2.dto.request.VoucherRequest;
import com.hcmute.g2webstorev2.dto.response.VoucherResponse;

import java.util.List;


public interface VoucherService {
    VoucherResponse addVoucher(VoucherRequest body);
    VoucherResponse getVoucher(String id);
    List<VoucherResponse> getVouchersByProduct(Integer id);
    void addVoucherToProducts(AddVoucherToProductRequest body);
}
