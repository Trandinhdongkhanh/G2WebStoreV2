package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.NotificationReq;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Notification;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.repository.NotificationRepo;
import com.hcmute.g2webstorev2.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepo notificationRepo;

    @Override
    public Page<Notification> getCusNotifications(int page, int size) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepo.getCusNotifications(
                customer.getCustomerId(),
                PageRequest.of(page, size, Sort.by("createdDate").descending()));
    }

    @Override
    public Page<Notification> getSellerNotifications(int page, int size) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return notificationRepo.getSellerNotifications(
                seller.getSellerId(),
                PageRequest.of(page, size, Sort.by("createdDate").descending()));
    }

    @Override
    public Page<Notification> getPublicNotifications(int page, int size) {
        return notificationRepo
                .findAllByIsPublic(PageRequest.of(page, size, Sort.by("createdDate").descending()));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Notification savePublicNotification(NotificationReq body) {
        LocalDateTime now = LocalDateTime.now();
        return notificationRepo.save(Notification.builder()
                .content(body.getContent())
                .createdDate(now)
                .isPublic(true)
                .build());
    }

    @Override
    @Transactional
    public Notification savePrivateNotification(NotificationReq body) {
        LocalDateTime now = LocalDateTime.now();
        return notificationRepo.save(Notification.builder()
                .content(body.getContent())
                .createdDate(now)
                .isPublic(false)
                .customerId(body.getCustomerId())
                .sellerId(body.getSellerId())
                .build());
    }
}