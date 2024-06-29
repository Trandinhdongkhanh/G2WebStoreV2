package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    Page<Order> findAllByCustomerOrderByOrderIdDesc(Customer customer, Pageable pageable);
    Page<Order> findAllByCustomerAndOrderStatusOrderByOrderIdDesc(Customer customer, OrderStatus orderStatus, Pageable pageable);
    Page<Order> findAllByShopAndOrderStatusOrderByOrderIdDesc(Shop shop, OrderStatus orderStatus, Pageable pageable);
    Page<Order> findAllByShopOrderByOrderIdDesc(Shop shop, Pageable pageable);
    @Query("select sum(o.grandTotal) from Order o " +
            "where o.shop.shopId = :shopId " +
            "and o.deliveredDate between :startTime and :endTime and " +
            "o.orderStatus = 'RECEIVED'")
    Long getShopIncome(Integer shopId, LocalDateTime startTime, LocalDateTime endTime);
    @Query("select count(*) from Order o where o.shop.shopId = :shopId " +
            "and o.orderStatus = 'RECEIVED'")
    long countSuccessOrder(Integer shopId);
    @Query("select count(*) from Order o where o.shop.shopId = :shopId " +
            "and o.orderStatus = 'DELIVERING'")
    long countDeliveringOrder(Integer shopId);
    @Query("select count(*) from Order o where o.shop.shopId = :shopId " +
            "and (" +
            "o.orderStatus = 'ORDERED' or " +
            "o.orderStatus = 'CONFIRMED' or " +
            "o.orderStatus = 'PACKED')")
    long countUnHandledOrder(Integer shopId);
    @Query("select count(*) from Order o where o.shop.shopId = :shopId " +
            "and o.orderStatus = 'CANCELED'")
    long countCanceledOrder(Integer shopId);
    @Query("select o from Order o where o.orderStatus = 'REFUNDING'")
    Page<Order> findAllRefundingOrders(Pageable pageable);
    @Query("select o from Order o where o.orderStatus = 'REFUNDED'")
    Page<Order> findAllRefundedOrders(Pageable pageable);
}
