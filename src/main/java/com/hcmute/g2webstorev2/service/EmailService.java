package com.hcmute.g2webstorev2.service;


import com.hcmute.g2webstorev2.entity.Order;

public interface EmailService {
    void sendVerificationCode(String verificationCode, String toEmail, String subject);
    void sendOrderConfirmation(Order order);
}
