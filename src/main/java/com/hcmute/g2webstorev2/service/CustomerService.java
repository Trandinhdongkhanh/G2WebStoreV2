package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ProblemDetail;

import java.io.IOException;

public interface CustomerService {
    CustomerResponse register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);

    AuthResponse refreshToken(String refreshToken);

    CustomerResponse getInfo();
}
