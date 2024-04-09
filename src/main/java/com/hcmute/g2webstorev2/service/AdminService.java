package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AdminResponse;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AdminService {
    AdminResponse register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);

    void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException;

    AdminResponse getInfo();
}
