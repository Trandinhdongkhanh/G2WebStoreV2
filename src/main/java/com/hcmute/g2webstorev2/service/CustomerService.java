package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.*;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ProblemDetail;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CustomerService {
    AuthResponse register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);

    AuthResponse refreshToken(String refreshToken);

    CustomerResponse getInfo();

    CustomerResponse updateProfile(CustomerProfileUpdateRequest body);

    void updatePassword(PasswordUpdateRequest body);

    void updateEmail(EmailUpdateRequest body);

    void updatePhoneNo(PhoneNoUpdateRequest body);

    CustomerResponse uploadAvatar(MultipartFile file);
}
