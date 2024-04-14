package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AddressRequest;
import com.hcmute.g2webstorev2.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddressesByCustomer();

    void updateAddressByCustomer(AddressRequest body, Integer id);

    AddressResponse addAddressByCustomer(AddressRequest body);

    void delAddressByCustomer(Integer id);
}
