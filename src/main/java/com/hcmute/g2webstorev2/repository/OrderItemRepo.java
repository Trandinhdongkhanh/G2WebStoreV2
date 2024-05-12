package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    @Query("select count(*) from OrderItem oi " +
            "where oi.order.shop.shopId = :shopId " +
            "and oi.isReviewed = false")
    long countUnReviewedItems(Integer shopId);
}
