package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.request.OrdersCreationRequest;
import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.dto.response.PaymentResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import com.hcmute.g2webstorev2.enums.PaymentType;
import com.hcmute.g2webstorev2.exception.NameNotMatchException;
import com.hcmute.g2webstorev2.exception.PriceNotMatchException;
import com.hcmute.g2webstorev2.exception.ProductNotSufficientException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.*;
import com.hcmute.g2webstorev2.service.EmailService;
import com.hcmute.g2webstorev2.service.OrderService;
import com.hcmute.g2webstorev2.service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.hcmute.g2webstorev2.enums.OrderStatus.*;
import static com.hcmute.g2webstorev2.enums.PaymentType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final CartItemRepo cartItemRepo;
    private final CustomerRepo customerRepo;
    private final ShopRepo shopRepo;
    private final AddressRepo addressRepo;
    private final VNPAYService vnpayService;
    private final VNPayTransRepo vnPayTransRepo;
    private final EmailService emailService;

    private void checkDataIntegrity(CartItemResponse item) {
        Product product = productRepo.findById(item.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with ID = " + item.getProductId() + " not found"));

        if (!Objects.equals(product.getPrice(), item.getPrice()))
            throw new PriceNotMatchException(
                    "Item price and product price does not match, please perform checkout again");

        if (!Objects.equals(item.getName(), product.getName()))
            throw new NameNotMatchException(
                    "Item name and product name does not match, please perform checkout again");

        if (product.getStockQuantity() == 0)
            throw new ProductNotSufficientException("Product with ID = "
                    + product.getProductId() + " is out of stock, please perform checkout again");

        if (product.getStockQuantity() < item.getQuantity())
            throw new ProductNotSufficientException("Product with ID = " + product.getProductId() + " is not" +
                    " sufficient, please adjust your quantity");
    }

    private Map<String, Object> handleOrderItemCreationProcess(List<CartItem> cartItems, Shop shop, Order order) {
        Map<String, Object> map = new HashMap<>();
        List<OrderItem> orderItems = new ArrayList<>();
        int total = 0;

        for (CartItem cartItem : cartItems) {
            if (!Objects.equals(cartItem.getProduct().getShop().getShopId(), shop.getShopId())) continue;

            OrderItem orderItem = OrderItem.builder()
                    .image(cartItem.getProduct().getImages().get(0).getFileUrl())
                    .price(cartItem.getProduct().getPrice())
                    .quantity(cartItem.getQuantity())
                    .name(cartItem.getProduct().getName())
                    .productId(cartItem.getProduct().getProductId())
                    .order(order)
                    .build();

            if (cartItem.getProduct().getSpecialPrice() != null) orderItem.setPrice(cartItem.getProduct().getSpecialPrice());

            orderItems.add(orderItem);

            Product product = cartItem.getProduct();

            product.setSoldQuantity(product.getSoldQuantity() + orderItem.getQuantity());
            product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());

            productRepo.save(product);
            log.info("Product with ID = " + product.getProductId() + " updated successfully");

            total += orderItem.getPrice() * orderItem.getQuantity();

            cartItemRepo.delete(cartItem);
        }

        map.put("total", total);
        map.put("orderItems", orderItems);
        return map;
    }

    private Order setUpOrder(PaymentType paymentType, Customer customer, Shop shop, Address address, Integer feeShip) {
        Order order = Order.builder()
                .createdDate(LocalDateTime.now())
                .customer(customer)
                .shop(shop)
                .feeShip(feeShip)
                .address(address)
                .build();

        switch (paymentType) {
            case COD -> {
                order.setPaymentType(COD);
                order.setOrderStatus(ORDERED);
            }
            case VNPAY -> {
                order.setPaymentType(VNPAY);
                order.setOrderStatus(UN_PAID);
            }
        }
        return order;
    }

    @Override
    @Transactional
    public List<OrderResponse> createOrders(OrdersCreationRequest body, HttpServletRequest req, HttpServletResponse res) throws IOException {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int ordersTotal = 0;
        double curPoint = customer.getPoint();

        Address address = addressRepo.findById(body.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address with ID = " + body.getAddressId() + " not found"));

        List<CartItem> cartItems = cartItemRepo.findAllByCustomer(customer);
        List<Order> orders = new ArrayList<>();

        if (cartItems.isEmpty())
            throw new ResourceNotFoundException("There are no items in cart, please add some products");

        log.info("Perform checking data integrity...");

        for (OrderCreationRequest order : body.getOrders()) {
            order.getItems().forEach(this::checkDataIntegrity);

            Shop shop = shopRepo.findById(order.getShopId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shop with ID = " + order.getShopId() + " not found"));

            Order newOrder = setUpOrder(body.getPaymentType(), customer, shop, address, order.getFeeShip());

            Map<String, Object> mapResult = handleOrderItemCreationProcess(cartItems, shop, newOrder);
            int total = (int) mapResult.get("total");
            List<OrderItem> orderItems = (List<OrderItem>) mapResult.get("orderItems");

            newOrder.setOrderItems(orderItems);
            newOrder.setTotal(total + order.getFeeShip());
            if (body.getIsPointSpent()) newOrder.setPointSpent((int) (curPoint / body.getOrders().size()));

            ordersTotal += total;

            customer.setPoint(customer.getPoint() + total * 0.05);
            customerRepo.save(customer);

            log.info("Point of customer with ID = " + customer.getCustomerId() + " updated successfully");

            Order result = orderRepo.save(newOrder);
            log.info("Order with ID = " + result.getOrderId() + " created successfully");
            orders.add(result);
        }

        if (body.getIsPointSpent()) ordersTotal -= curPoint;

        if (!body.getPaymentType().equals(COD)) processOnlPayment(body.getPaymentType(), ordersTotal, req, res, orders);
        else orders.forEach(emailService::sendOrderConfirmation);

        return orders.stream()
                .map(Mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    private void processOnlPayment(PaymentType paymentType, int total, HttpServletRequest req, HttpServletResponse res,
                                   List<Order> orders) throws IOException {
        if (paymentType.equals(VNPAY)) {
            PaymentResponse paymentResponse = vnpayService.createPayment(total, null, null, req);
            log.info(paymentResponse.getPaymentUrl());

            orders.forEach(order ->
                    vnPayTransRepo.save(VNPAYTransaction.builder()
                            .order(order)
                            .vnp_TxnRef(paymentResponse.getVnp_TxnRef())
                            .trans_date(null)
                            .build())
            );

            res.sendRedirect(paymentResponse.getPaymentUrl());
        }
    }

    @Override
    public Page<OrderResponse> getMyOrders(OrderStatus orderStatus, int pageNumber, int pageSize) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (orderStatus == null)
            return orderRepo
                    .findAllByCustomerOrderByOrderIdDesc(customer, PageRequest.of(pageNumber, pageSize))
                    .map(Mapper::toOrderResponse);

        return orderRepo
                .findAllByCustomerAndOrderStatusOrderByOrderIdDesc(customer, orderStatus, PageRequest.of(pageNumber, pageSize))
                .map(Mapper::toOrderResponse);
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
            throw new AccessDeniedException("You don't have permission to update the Order Status to '" + RECEIVED.name() + "'" +
                    ", please update after 7 days from delivered date");

        order.setOrderStatus(status);
        log.info("Order status of order with ID = " + id + " have been updated to '" + status.name() + "' successfully");
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

        if (status.equals(RECEIVED)) {
            Shop shop = order.getShop();
            shop.setBalance(shop.getBalance() + order.getTotal());
            shopRepo.save(shop);
        }

        return Mapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void updateUnPaidOrder(String vnp_TxnRef) {
        List<VNPAYTransaction> vnpayTransactions = vnPayTransRepo.findAllByVnp_TxnRef(vnp_TxnRef);
        if (vnpayTransactions.isEmpty()) throw new ResourceNotFoundException("Transactions not found");

        vnpayTransactions.forEach(vnpayTransaction -> {
            Order order = vnpayTransaction.getOrder();
            order.setOrderStatus(ORDERED);
            orderRepo.save(order);
            emailService.sendOrderConfirmation(order);
        });
    }

    @Override
    public Page<OrderResponse> getShopOrders(OrderStatus orderStatus, int pageNumber, int pageSize) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (orderStatus == null)
            return orderRepo
                    .findAllByShopOrderByOrderIdDesc(seller.getShop(), PageRequest.of(pageNumber, pageSize))
                    .map(Mapper::toOrderResponse);
        return orderRepo
                .findAllByShopAndOrderStatusOrderByOrderIdDesc(seller.getShop(), orderStatus, PageRequest.of(pageNumber, pageSize))
                .map(Mapper::toOrderResponse);
    }

    private boolean isSevenDaysPassed(LocalDateTime deliveredDate, LocalDateTime curDate) {
        return (ChronoUnit.DAYS.between(deliveredDate, curDate) >= 7);
    }
}