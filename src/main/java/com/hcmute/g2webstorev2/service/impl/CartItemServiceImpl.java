package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import com.hcmute.g2webstorev2.entity.CartItem;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemRepo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.service.CartItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepo cartItemRepo;
    @Autowired
    private ProductRepo productRepo;

    @Override
    public List<CartItemResponse> getAllItems() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return cartItemRepo.findAllByCustomer(customer)
                .stream().map(cartItem -> {
                    Integer subTotal = cartItem.getQuantity() * cartItem.getPrice();
                    cartItem.setSubTotal(subTotal);
                    return Mapper.toCartItemResponse(cartItem);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartItemResponse addItem(CartItemRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product product = productRepo.findById(body.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + body.getProductId() + " not found"));

        CartItem cartItem = cartItemRepo.findByProductAndCustomer(product, customer)
                        .orElse(null);
        if (cartItem == null){
            CartItemResponse res = Mapper.toCartItemResponse(cartItemRepo.save(CartItem.builder()
                    .price(product.getPrice())
                    .name(product.getName())
                    .quantity(body.getQuantity())
                    .customer(customer)
                    .product(product)
                    .build()));

            log.info("Add item to cart successfully");
            return res;
        }

        cartItem.setQuantity(cartItem.getQuantity() + body.getQuantity());
        CartItemResponse res = Mapper.toCartItemResponse(cartItem);
        log.info("Add item to cart successfully");

        return res;
    }

    @Override
    public void delItem(Integer productId) {

    }

    @Override
    public CartItemResponse updateItem(CartItemRequest body) {
        return null;
    }
}
