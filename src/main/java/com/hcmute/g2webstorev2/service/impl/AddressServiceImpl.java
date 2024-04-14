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
                .stream().map(Mapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAddressByCustomer(AddressRequest body, Integer id) {
        Address address = addressRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address with ID = " + id + " not found"));

        address.setOrderReceiveAddress(body.getOrderReceiveAddress());
        address.setWard(body.getWard());
        address.setDistrictId(body.getDistrictId());
        address.setDistrict(body.getDistrict());
        address.setProvince(body.getProvince());
        address.setReceiverName(body.getReceiverName());
        address.setReceiverPhoneNo(body.getReceiverPhoneNo());
        address.setDefault(body.isDefault());

        log.info("Update address with ID = " + id + " successfully");
    }

    @Override
    @Transactional
    public AddressResponse addAddressByCustomer(AddressRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        AddressResponse res = Mapper.toAddressResponse(addressRepo.save(
                Address.builder()
                        .customer(customer)
                        .province(body.getProvince())
                        .district(body.getDistrict())
                        .districtId(body.getDistrictId())
                        .ward(body.getWard())
                        .orderReceiveAddress(body.getOrderReceiveAddress())
                        .receiverPhoneNo(body.getReceiverPhoneNo())
                        .receiverName(body.getReceiverName())
                        .isDefault(false)
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

        addressRepo.delete(address);
        log.info("Address with ID = " + id + " deleted successfully");
    }
}
