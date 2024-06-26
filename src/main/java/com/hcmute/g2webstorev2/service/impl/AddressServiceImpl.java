package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.AddressRequest;
import com.hcmute.g2webstorev2.dto.response.AddressResponse;
import com.hcmute.g2webstorev2.entity.Address;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.AddressRepo;
import com.hcmute.g2webstorev2.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepo addressRepo;

    @Override
    public List<AddressResponse> getAddressesByCustomer() {
        log.info("Begin fetching customer addresses...");
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return addressRepo.findAllByCustomer(customer)
                .stream().filter(address -> !address.isDeleted())
                .map(Mapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAddressByCustomer(AddressRequest body, Integer id) {
        Address address = addressRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address with ID = " + id + " not found"));

        if (body.isDefault()){
            addressRepo.findByCustomerAndDefaultIsTrue(address.getCustomer())
                     .ifPresent(defaultAddress -> defaultAddress.setDefault(false));
        }

        address.setOrderReceiveAddress(body.getOrderReceiveAddress());
        address.setWardCode(body.getWardCode());
        address.setWardName(body.getWardName());
        address.setDistrictId(body.getDistrictId());
        address.setDistrictName(body.getDistrictName());
        address.setProvinceId(body.getProvinceId());
        address.setProvinceName(body.getProvinceName());
        address.setReceiverName(body.getReceiverName());
        address.setReceiverPhoneNo(body.getReceiverPhoneNo());
        address.setDefault(body.isDefault());

        log.info("Update address with ID = " + id + " successfully");
    }

    @Override
    @Transactional
    public AddressResponse addAddressByCustomer(AddressRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (body.isDefault()){
            addressRepo.findByCustomerAndDefaultIsTrue(customer)
                    .ifPresent(defaultAddress -> defaultAddress.setDefault(false));
        }

        AddressResponse res = Mapper.toAddressResponse(addressRepo.save(
                Address.builder()
                        .customer(customer)
                        .provinceId(body.getProvinceId())
                        .provinceName(body.getProvinceName())
                        .districtId(body.getDistrictId())
                        .districtName(body.getDistrictName())
                        .wardCode(body.getWardCode())
                        .wardName(body.getWardName())
                        .orderReceiveAddress(body.getOrderReceiveAddress())
                        .receiverPhoneNo(body.getReceiverPhoneNo())
                        .receiverName(body.getReceiverName())
                        .isDefault(body.isDefault())
                        .isDeleted(false)
                        .build()
        ));

        log.info("Add address successfully");
        return res;
    }

    @Override
    @Transactional
    public void delAddressByCustomer(Integer id) {
        Address address = addressRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address with ID = " + id + " not found"));

        address.setDeleted(true);
        log.info("Address with ID = " + id + " deleted successfully");
    }
}
