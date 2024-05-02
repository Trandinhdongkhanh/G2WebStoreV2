package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    Page<Order> findAllByCustomerOrderByOrderIdDesc(Customer customer, Pageable pageable);
    Page<Order> findAllByCustomerAndOrderStatusOrderByOrderIdDesc(Customer customer, OrderStatus orderStatus, Pageable pageable);
    Page<Order> findAllByShopAndOrderStatusOrderByOrderIdDesc(Shop shop, OrderStatus orderStatus, Pageable pageable);
    Page<Order> findAllByShopOrderByOrderIdDesc(Shop shop, Pageable pageable);
}
