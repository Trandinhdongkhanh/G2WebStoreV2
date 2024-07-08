package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.NotificationReq;
import com.hcmute.g2webstorev2.entity.Notification;
import com.hcmute.g2webstorev2.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/api/v1/notifications/customer/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<Notification>> getCusNotifications(
            @RequestParam(defaultValue = "0", name = "page", required = false) int page,
            @RequestParam(defaultValue = "5", name = "size", required = false) int size
    ) {
        return ResponseEntity.ok(notificationService.getCusNotifications(page, size));
    }

    @GetMapping("/api/v1/notifications/seller/me")
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_PROMOTION_ACCESS'," +
            "'SELLER_PRODUCT_ACCESS'," +
            "'JUNIOR_CHAT_AGENT'," +
            "'SELLER_ORDER_MANAGEMENT'," +
            "'SELLER_FULL_ACCESS'," +
            "'SELLER_READ_ONLY')")
    public ResponseEntity<Page<Notification>> getSellerNotifications(
            @RequestParam(defaultValue = "0", name = "page", required = false) int page,
            @RequestParam(defaultValue = "5", name = "size", required = false) int size
    ) {
        return ResponseEntity.ok(notificationService.getSellerNotifications(page, size));
    }

    // Mapped as /app/public
    @MessageMapping("/public")
    @SendTo("/all/notifications")
    public Notification send(@Payload NotificationReq body) {
        return notificationService.savePublicNotification(body);
    }

    // Mapped as /app/private
    @MessageMapping("/private/customer")
    public void sendToSpecificCus(@Payload NotificationReq body) {
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(body.getCustomerId()),
                "/specific",
                notificationService.savePrivateNotification(body));
    }

    @MessageMapping("/private/seller")
    public void sendToSpecificSeller(@Payload NotificationReq body) {
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(body.getSellerId()),
                "/specific",
                notificationService.savePrivateNotification(body));
    }
}
