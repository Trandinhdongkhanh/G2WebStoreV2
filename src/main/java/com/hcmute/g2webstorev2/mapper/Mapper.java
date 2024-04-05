package com.hcmute.g2webstorev2.mapper;

import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Seller;

public class Mapper {
    public static CustomerResponse toCustomerResponse(Customer customer){
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .email(customer.getEmail())
                .phoneNo(customer.getPhoneNo())
                .fullName(customer.getFullName())
                .avatar(customer.getAvatar())
                .point(customer.getPoint())
                .dob(customer.getDob())
                .isEmailVerified(customer.isEmailVerified())
                .role(customer.getRole())
                .build();
    }

    public static SellerResponse toSellerResponse(Seller seller) {
        return SellerResponse.builder()
                .sellerId(seller.getSellerId())
                .email(seller.getEmail())
                .phoneNo(seller.getPhoneNo())
                .fullName(seller.getFullName())
                .avatar(seller.getAvatar())
                .isEmailVerified(seller.isEmailVerified())
                .role(seller.getRole())
                .shop(seller.getShop())
                .build();
    }
}
