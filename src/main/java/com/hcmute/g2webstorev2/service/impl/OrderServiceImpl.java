package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import com.hcmute.g2webstorev2.exception.NameNotMatchException;
import com.hcmute.g2webstorev2.exception.PriceNotMatchException;
import com.hcmute.g2webstorev2.exception.ProductNotSufficientException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.*;
import com.hcmute.g2webstorev2.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private CartItemRepo cartItemRepo;
    @Autowired
    private CustomerRepo customerRepo;

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
            if (product.getStockQuantity() == 0)
                throw new ProductNotSufficientException("Product with ID = "
                        + product.getProductId() + " is out of stock, please perform checkout again");
            if (product.getStockQuantity() < item.getQuantity())
                throw new ProductNotSufficientException("Product with ID = " + product.getProductId() + " is not" +
                        " sufficient, please adjust your quantity");

            shops.add(product.getShop());
            productMap.put(product.getProductId(), product);
        });

        List<Order> orders = new ArrayList<>();

        shops.forEach(shop -> {
            List<OrderItem> orderItems = new ArrayList<>();

            Order order = Order.builder()
                    .orderStatus(ORDERED)
                    .createdDate(LocalDateTime.now())
                    .curDate(LocalDateTime.now())
                    .customer(customer)
                    .shop(shop)
                    .build();

            int total = 0;

            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = OrderItem.builder()
                        .image(cartItem.getProduct().getImages())
                        .price(cartItem.getProduct().getPrice())
                        .quantity(cartItem.getQuantity())
                        .name(cartItem.getProduct().getName())
                        .productId(cartItem.getProduct().getProductId())
                        .order(order)
                        .build();

                orderItems.add(orderItem);

                Product product = productMap.get(orderItem.getProductId());

                product.setSoldQuantity(product.getSoldQuantity() + orderItem.getQuantity());
                product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());

                productRepo.save(product);
                log.info("Product with ID = " + product.getProductId() + " updated successfully");

                total += orderItem.getPrice() * orderItem.getQuantity();
            }

            order.setOrderItems(orderItems);
            order.setTotal(total + body.getFeeShip());

            customer.setPoint(customer.getPoint() + total * 0.05);
            customerRepo.save(customer);

            log.info("Point of customer with ID = " + customer.getCustomerId() + " updated successfully");

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

    @Override
    @Transactional
    public OrderResponse sellerUpdateOrderStatus(Integer id, OrderStatus status) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID = " + id + " not found"));

        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(seller.getShop().getShopId(), order.getShop().getShopId()))
            throw new AccessDeniedException("You don't have permission on this order, access denied");

        if (status == RECEIVED && !isSevenDaysPassed(order.getDeliveredDate(), LocalDateTime.now()))
            throw new AccessDeniedException("You don't have permission to update the Order Status to '" + RECEIVED + "'" +
                    ", please update after 7 days from delivered date");

        order.setOrderStatus(status);
        log.info("Order status of order with ID = " + id + " have been updated to '" + status + "' successfully");
        return Mapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse customerUpdateOrderStatus(Integer id, OrderStatus status) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID = " + id + " not found"));

        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(order.getCustomer().getCustomerId(), customer.getCustomerId()))
            throw new AccessDeniedException("You don't have permission on this order, access denied");

        order.setOrderStatus(status);
        log.info("Order status of order with ID = " + id + " have been updated to '" + status + "' successfully");
        return Mapper.toOrderResponse(order);
    }

    private boolean isSevenDaysPassed(LocalDateTime deliveredDate, LocalDateTime curDate) {
        return (ChronoUnit.DAYS.between(deliveredDate, curDate) >= 7);
    }
}
