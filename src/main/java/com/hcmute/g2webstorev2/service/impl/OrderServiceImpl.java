package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.entity.CartItem;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.exception.NameNotMatchException;
import com.hcmute.g2webstorev2.exception.PriceNotMatchException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemRepo;
import com.hcmute.g2webstorev2.repository.OrderItemRepo;
import com.hcmute.g2webstorev2.repository.OrderRepo;
import com.hcmute.g2webstorev2.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private OrderItemRepo orderItemRepo;
    @Autowired
    private CartItemRepo cartItemRepo;

    @Override
    public List<OrderResponse> getOrders() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return orderRepo.findAllByCustomer(customer)
                .stream().map(Mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByCustomer(Integer id) {
        return null;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreationRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CartItem> items = cartItemRepo.findAllByCustomer(customer);

        validateDataIntegrity(items);

        return null;
    }

    private void validateDataIntegrity(List<CartItem> items) {
        items.forEach(item -> {
            if (!Objects.equals(item.getPrice(), item.getProduct().getPrice()))
                throw new PriceNotMatchException("Item price and product price does not match, please perform checkout again");
            if (!Objects.equals(item.getName(), item.getProduct().getName()))
                throw new NameNotMatchException("Item name and product name does not match, please perform checkout again");
        });
    }

    @Override
    public List<OrderResponse> getOrdersByShop() {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return orderRepo.findAllByShop(seller.getShop())
                .stream().map(Mapper::toOrderResponse)
                .collect(Collectors.toList());
    }
}
