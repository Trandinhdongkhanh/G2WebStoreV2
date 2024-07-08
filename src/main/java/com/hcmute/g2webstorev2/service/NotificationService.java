package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.NotificationReq;
import com.hcmute.g2webstorev2.entity.Notification;
import org.springframework.data.domain.Page;

public interface NotificationService {
    Page<Notification> getCusNotifications(int page, int size);
    Page<Notification> getSellerNotifications(int page, int size);
    Notification savePublicNotification(NotificationReq body);
    Notification savePrivateNotification(NotificationReq body);
}
