package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n where n.isPublic = true or n.customerId = :customerId")
    Page<Notification> getCusNotifications(Integer customerId, Pageable pageable);
    @Query("select n from Notification n where n.isPublic = true or n.sellerId = :sellerId")
    Page<Notification> getSellerNotifications(Integer sellerId, Pageable pageable);
    @Query("select n from Notification n where n.isPublic = true")
    Page<Notification> findAllByIsPublic(Pageable pageable);
}
