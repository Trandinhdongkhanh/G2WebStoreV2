package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import com.hcmute.g2webstorev2.dto.response.CartResponseV2;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImplV2 {
    @Autowired
    private CartItemRepo cartItemRepo;
    public List<CartResponseV2> getAllCartItems(){
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CartItemResponse> items = cartItemRepo.findAllByCustomer(customer)
                .stream().map(Mapper::toCartItemResponse)
                .toList();
        return null;
    }
}
