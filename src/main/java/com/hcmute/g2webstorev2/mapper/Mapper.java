package com.hcmute.g2webstorev2.mapper;

import com.hcmute.g2webstorev2.dto.response.AdminResponse;
import com.hcmute.g2webstorev2.dto.response.CategoryResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import com.hcmute.g2webstorev2.entity.Admin;
import com.hcmute.g2webstorev2.entity.Category;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Seller;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Mapper {
    public static CustomerResponse toCustomerResponse(Customer customer) {
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

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .childCategories(category.getChildCategories())
                .build();
    }

    public static AdminResponse toAdminResponse(Admin admin) {
        return AdminResponse.builder()
                .adminId(admin.getAdminId())
                .email(admin.getEmail())
                .role(admin.getRole())
                .build();
    }
}
