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
    @Transactional
    public List<OrderResponse> createOrders(OrderCreationRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CartItem> cartItems = cartItemRepo.findAllByCustomer(customer);

        if (cartItems.isEmpty())
            throw new ResourceNotFoundException("There are no items in cart, please add some products");

        Set<Shop> shops = new HashSet<>();

        log.info("Perform checking data integrity...");

        Map<Integer, Product> productMap = new HashMap<>();

        body.getItems().forEach(item -> {
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + item.getProductId() + " not found"));

            if (!Objects.equals(product.getPrice(), item.getPrice()))
                throw new PriceNotMatchException("Item price and product price does not match, please perform checkout again");
            if (!Objects.equals(item.getName(), product.getName()))
                throw new NameNotMatchException("Item name and product name does not match, please perform checkout again");

            shops.add(product.getShop());
            productMap.put(product.getProductId(), product);
        });

        List<Order> orders = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();

        shops.forEach(shop -> {
            Order order = Order.builder()
                    .orderStatus(ORDERED)
                    .createdDate(LocalDateTime.now())
                    .curDate(LocalDateTime.now())
                    .customer(customer)
                    .shop(shop)
                    .build();

            int total = 0;

            for (CartItem cartItem : cartItems){
                OrderItem orderItem = orderItemRepo.save(OrderItem.builder()
                        .image(cartItem.getProduct().getImages())
                        .price(cartItem.getProduct().getPrice())
                        .quantity(cartItem.getQuantity())
                        .name(cartItem.getProduct().getName())
                        .order(order)
                        .productId(cartItem.getProduct().getProductId())
                        .build());

                orderItems.add(orderItem);

                Integer curStockQuantity = cartItem.getProduct().getStockQuantity();
                Integer curSoldQuantity = cartItem.getProduct().getSoldQuantity();

                Product product = productMap.get(orderItem.getProductId());
                product.setSoldQuantity(curSoldQuantity + orderItem.getQuantity());
                product.setStockQuantity(curStockQuantity - orderItem.getQuantity());

                productRepo.save(product);
                log.info("Product with ID = " + product.getProductId() + " updated successfully");

                total += orderItem.getPrice() * orderItem.getQuantity();
            }

            order.setOrderItems(orderItems);
            order.setTotal(total);

            Order res = orderRepo.save(order);
            log.info("Order with ID = " + res.getOrderId() + " created successfully");
            orders.add(res);
        });

        cartItemRepo.deleteAllByCustomer(customer);

        log.info("All items owned by customer with ID = " + customer.getCustomerId() + " deleted successfully");

        return orders.stream()
                .map(Mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getMyOrders() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return orderRepo.findAllByCustomer(customer)
                .stream().map(Mapper::toOrderResponse)
                .collect(Collectors.toList());
    }
}
