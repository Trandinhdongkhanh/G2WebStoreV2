package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.CartItemRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import com.hcmute.g2webstorev2.entity.CartItem;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.CartItemCompositeKey;
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
                    cartItem.setSubTotal(cartItem.getProduct().getPrice() * cartItem.getQuantity());
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

        if (cartItem == null) {
            cartItem = cartItemRepo.save(CartItem.builder()
                    .cartItemCompositeKey(new CartItemCompositeKey(customer.getCustomerId(), product.getProductId()))
                    .quantity(body.getQuantity())
                    .customer(customer)
                    .product(product)
                    .build());
            cartItem = cartItemRepo.save(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + body.getQuantity());
        }

        int price = product.getPrice();
        cartItem.setSubTotal(cartItem.getQuantity() * price);
        CartItemResponse res = Mapper.toCartItemResponse(cartItem);
        log.info("Add item to cart successfully");

        return res;
    }

    @Override
    @Transactional
    public void delItem(Integer productId) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + productId + " not found"));

        CartItem cartItem = cartItemRepo.findByProductAndCustomer(product, customer)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + productId +
                        " not existed in customer cart"));

        cartItem.setProduct(null);
        cartItem.setCustomer(null);

        cartItemRepo.delete(cartItem);
        log.info("Item with ID = " + productId + " deleted successfully");
    }

    @Override
    @Transactional
    public CartItemResponse updateItem(CartItemRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product product = productRepo.findById(body.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + body.getProductId() + " not found"));

        CartItem cartItem = cartItemRepo.findByProductAndCustomer(product, customer)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + body.getProductId() +
                        " not existed in customer cart"));

        int price = product.getPrice();

        cartItem.setQuantity(body.getQuantity());
        cartItem.setSubTotal(price * body.getQuantity());

        log.info("Item with ID = " + body.getProductId() + " updated successfully");
        return Mapper.toCartItemResponse(cartItem);
    }
}
