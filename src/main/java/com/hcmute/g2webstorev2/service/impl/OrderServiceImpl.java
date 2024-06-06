package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.CheckShopItemReq;
import com.hcmute.g2webstorev2.dto.request.OrderCreationRequest;
import com.hcmute.g2webstorev2.dto.request.OrdersCreationRequest;
import com.hcmute.g2webstorev2.dto.response.OrderResponse;
import com.hcmute.g2webstorev2.dto.response.OrdersCreationResponse;
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
import java.io.UnsupportedEncodingException;
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
    private final ShopRepo shopRepo;
    private final AddressRepo addressRepo;
    private final VNPAYService vnpayService;
    private final VNPayTransRepo vnPayTransRepo;
    private final EmailService emailService;
    private final CartItemV2Repo cartItemV2Repo;
    private final CustomerRepo customerRepo;

    private void checkDataIntegrity(CheckShopItemReq item) {
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

    private Order setUpOrder(PaymentType paymentType, Customer customer, CartItemV2 cartItemV2, Address address, Integer feeShip) {
        Order order = Order.builder()
                .createdDate(LocalDateTime.now())
                .customer(customer)
                .shop(cartItemV2.getShop())
                .feeShip(feeShip)
                .feeShipReduce(Math.toIntExact(cartItemV2.getFeeShipReduce()))
                .shopVoucherPriceReduce(Math.toIntExact(cartItemV2.getShopReduce()))
                .total(Math.toIntExact(cartItemV2.getShopSubTotal()))
                .pointSpent(0)
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
    public OrdersCreationResponse createOrders(OrdersCreationRequest body, HttpServletRequest req, HttpServletResponse res) throws IOException {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Address address = addressRepo.findById(body.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Set<CartItemV2> cartItemV2Set = cartItemV2Repo.findAllByCustomer(customer);
        List<Order> orders = new ArrayList<>();
        int ordersTotalPrice = 0;

        for (OrderCreationRequest order : body.getOrders()) {
            order.getItems().forEach(this::checkDataIntegrity);
            Order newOrder = null;
            for (CartItemV2 cartItemV2 : cartItemV2Set) {
                newOrder = setUpOrder(body.getPaymentType(), customer, cartItemV2, address, order.getFeeShip());
                List<OrderItem> orderItems = handleOrderItemCreationProcess(cartItemV2, newOrder);
                newOrder.setOrderItems(orderItems);
            }
            if (newOrder != null) {
                if (body.getIsPointSpent()) {
                    Integer pointSpent = (int) (customer.getPoint() / cartItemV2Set.size());
                    newOrder.setPointSpent(pointSpent);
                    customer.setPoint(0);
                    customerRepo.save(customer);
                }
                ordersTotalPrice += newOrder.getTotal() + newOrder.getFeeShip() - newOrder.getShopVoucherPriceReduce()
                        - newOrder.getFeeShipReduce() - newOrder.getPointSpent();
                orders.add(newOrder);
            }
        }
        List<Order> result = orderRepo.saveAll(orders);
        cartItemV2Repo.deleteAll(cartItemV2Set);

        String paymentUrl = null;
        if (!body.getPaymentType().equals(COD))
            paymentUrl = processOnlPayment(body.getPaymentType(), ordersTotalPrice, req, orders);
        else orders.forEach(emailService::sendOrderConfirmation);

        return OrdersCreationResponse.builder()
                .orders(result.stream().map(Mapper::toOrderResponse).collect(Collectors.toList()))
                .paymentUrl(paymentUrl)
                .build();
    }

    private List<OrderItem> handleOrderItemCreationProcess(CartItemV2 cartItemV2, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (ShopItem shopItem : cartItemV2.getShopItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .image(shopItem.getProduct().getImages().get(0).getFileUrl())
                    .price(shopItem.getProduct().getPrice())
                    .quantity(shopItem.getQuantity())
                    .name(shopItem.getProduct().getName())
                    .productId(shopItem.getProduct().getProductId())
                    .isReviewed(false)
                    .order(order)
                    .build();

            orderItems.add(orderItem);

            Product product = shopItem.getProduct();

            product.setSoldQuantity(product.getSoldQuantity() + orderItem.getQuantity());
            product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());

            productRepo.save(product);
            log.info("Product with ID = " + product.getProductId() + " updated successfully");
        }
        return orderItems;
    }

    private String processOnlPayment(PaymentType paymentType, int total, HttpServletRequest req, List<Order> orders) throws IOException {
        if (paymentType.equals(VNPAY)) {
            PaymentResponse paymentResponse = vnpayService.createPayment(total, null, null, req);
            log.info(paymentResponse.getPaymentUrl());

            orders.forEach(order ->
                    vnPayTransRepo.save(VNPAYTransaction.builder()
                            .order(order)
                            .vnp_TxnRef(paymentResponse.getVnp_TxnRef())
                            .trans_date(paymentResponse.getVnp_CreateDate())
                            .build())
            );
            return paymentResponse.getPaymentUrl();
        }
        return null;
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

        if (order.getOrderStatus().equals(UN_PAID))
            throw new AccessDeniedException("Order is UNPAID, can't change status");
        if (status.equals(DELIVERED)) order.setDeliveredDate(LocalDateTime.now());


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

        if (status.equals(REFUNDED))
            throw new AccessDeniedException("Can't update order status to REFUNDED");

        return Mapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void updateUnPaidOrder(String vnp_TxnRef) {
        List<VNPAYTransaction> vnpayTransactions = vnPayTransRepo.findAllByVnp_TxnRef(vnp_TxnRef);
        if (vnpayTransactions.isEmpty()) throw new ResourceNotFoundException("Transactions not found");

        List<Order> orders = new ArrayList<>();
        vnpayTransactions.forEach(transaction -> {
            Order order = transaction.getOrder();
            order.setOrderStatus(ORDERED);
            orders.add(order);
        });
        orderRepo.saveAll(orders);
        orders.forEach(emailService::sendOrderConfirmation);
    }

    @Override
    @Transactional
    public PaymentResponse payUnPaidOrder(Integer orderId, HttpServletRequest req) throws UnsupportedEncodingException {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        PaymentResponse paymentResponse = vnpayService.createPayment(order.getTotal(), null, null, req);
        vnPayTransRepo.deleteAllByOrder(order);
        vnPayTransRepo.save(VNPAYTransaction.builder()
                .vnp_TxnRef(paymentResponse.getVnp_TxnRef())
                .order(order)
                .total(order.getTotal())
                .trans_date(paymentResponse.getVnp_CreateDate())
                .build());

        return paymentResponse;
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

    @Override
    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return Mapper.toOrderResponse(order);
    }

    private boolean isSevenDaysPassed(LocalDateTime deliveredDate, LocalDateTime curDate) {
        return (ChronoUnit.DAYS.between(deliveredDate, curDate) >= 7);
    }
}