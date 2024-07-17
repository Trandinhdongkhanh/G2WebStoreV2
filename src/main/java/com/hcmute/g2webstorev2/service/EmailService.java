package com.hcmute.g2webstorev2.service;


import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.entity.Product;

public interface EmailService {
    void sendVerificationCode(String verificationCode, String toEmail, String subject);
    void sendOrderConfirmation(Order order);
    void sendLockShopNotification(String subject, String toEmail);
    void sendLockedProductNotification(String subject, String toEmail, Product product, String reason);
}
