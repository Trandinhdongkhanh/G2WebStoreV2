package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.exception.EmailException;
import com.hcmute.g2webstorev2.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String fromEmail;


    @Override
    @Async
    public void sendVerificationCode(String verificationCode, String toEmail, String subject) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setTo(toEmail);

            Map<String, Object> properties = new HashMap<>();
            properties.put("email", toEmail);
            properties.put("verification_code", verificationCode);

            Context context = new Context();
            context.setVariables(properties);

            String template = templateEngine.process("email_verification", context);

            helper.setText(template, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOrderConfirmation(Order order) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            int discount = 0;
            if (order.getG2VoucherPriceReduce() != null) discount += order.getG2VoucherPriceReduce();
            if (order.getShopVoucherPriceReduce() != null) discount += order.getShopVoucherPriceReduce();
            if (order.getFeeShipReduce() != null) discount += order.getFeeShipReduce();

            helper.setFrom(fromEmail);
            helper.setSubject("G2Store xác nhận đơn hàng số #" + order.getOrderId());
            helper.setTo(order.getCustomer().getEmail());

            Map<String, Object> properties = new HashMap<>();
            properties.put("email", order.getCustomer().getEmail());
            properties.put("order", order);
            properties.put("receiverName", order.getAddress().getReceiverName());
            properties.put("district", order.getAddress().getDistrictName());
            properties.put("province", order.getAddress().getProvinceName());
            properties.put("ward", order.getAddress().getWardName());
            properties.put("address", order.getAddress().getOrderReceiveAddress());
            properties.put("phoneNo", order.getAddress().getReceiverPhoneNo());
            properties.put("shopName", order.getShop().getName());
            properties.put("deliveredDate", order.getDeliveredDate());
            properties.put("orderItems", order.getOrderItems());
            properties.put("total", order.getShopTotal());
            properties.put("feeShip", order.getFeeShip());
            properties.put("discount", discount);
            properties.put("finalTotal", order.getGrandTotal());
            properties.put("paymentType", order.getPaymentType());

            Context context = new Context();
            context.setVariables(properties);

            String template = templateEngine.process("order_confirmation", context);

            helper.setText(template, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
        }
    }

    @Override
    public void sendLockShopNotification(String subject, String toEmail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setTo(toEmail);

            Map<String, Object> properties = new HashMap<>();
            properties.put("email", toEmail);

            Context context = new Context();
            context.setVariables(properties);

            String template = templateEngine.process("lock_shop_notification", context);

            helper.setText(template, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
        }
    }

    @Override
    public void sendLockedProductNotification(String subject, String toEmail, Product product, String reason) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setTo(toEmail);

            Map<String, Object> properties = new HashMap<>();
            properties.put("email", toEmail);
            properties.put("reason", reason);
            properties.put("id", product.getProductId().toString());

            Context context = new Context();
            context.setVariables(properties);

            String template = templateEngine.process("lock_product_notification", context);

            helper.setText(template, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new EmailException(e.getMessage());
        }
    }
}
