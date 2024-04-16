package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.exception.NameNotMatchException;
import com.hcmute.g2webstorev2.exception.PriceNotMatchException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CartItemRepo;
import com.hcmute.g2webstorev2.repository.OrderItemRepo;
import com.hcmute.g2webstorev2.repository.OrderRepo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.hcmute.g2webstorev2.enums.OrderStatus.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductRepo productRepo;
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
    public List<OrderResponse> createOrders(OrderCreationRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<Shop> shops = new HashSet<>();

        log.info("Perform checking data integrity...");

        body.getItems().forEach(item -> {
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + item.getProductId() + " not found"));

            if (!Objects.equals(product.getPrice(), item.getPrice()))
                throw new PriceNotMatchException("Item price and product price does not match, please perform checkout again");
            if (!Objects.equals(item.getName(), product.getName()))
                throw new NameNotMatchException("Item name and product name does not match, please perform checkout again");

            shops.add(product.getShop());
        });

        List<Order> orders = new ArrayList<>();

        shops.forEach(shop -> {
            Order order = orderRepo.save(Order.builder()
                    .orderStatus(ORDERED)
                    .createdDate(LocalDateTime.now())
                    .curDate(LocalDateTime.now())
                    .customer(customer)
                    .shop(shop)
                    .build());

            log.info("Order with ID = " + order.getOrderId() + " created successfully");

            orders.add(order);

            body.getItems().forEach(item -> {
                orderItemRepo.save(OrderItem.builder()
                        .image(item.getImages())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .name(item.getName())
                        .order(order)
                        .build());
            });
        });

        cartItemRepo.deleteAllByCustomer(customer);

        log.info("All items owned by customer with ID = " + customer.getCustomerId() + " deleted successfully");

        return orders.stream()
                .map(Mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByShop() {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return orderRepo.findAllByShop(seller.getShop())
                .stream().map(Mapper::toOrderResponse)
                .collect(Collectors.toList());
    }
}
