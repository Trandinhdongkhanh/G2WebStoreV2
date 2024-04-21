package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findAllByCustomerOrderByOrderIdDesc(Customer customer);
    List<Order> findAllByCustomerAndOrderStatus(Customer customer, OrderStatus orderStatus);
}
