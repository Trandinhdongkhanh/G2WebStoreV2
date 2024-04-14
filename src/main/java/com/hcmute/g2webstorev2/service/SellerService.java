package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

public interface SellerService {
    AuthResponse register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);

    AuthResponse refreshToken (String refreshToken);

    SellerResponse getInfo();
}
