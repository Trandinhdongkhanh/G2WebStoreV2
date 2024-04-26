package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.entity.Order;
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

    }
}
