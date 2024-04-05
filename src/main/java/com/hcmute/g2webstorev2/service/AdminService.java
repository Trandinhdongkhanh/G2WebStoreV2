package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AdminResponse;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;

public interface AdminService {
    AdminResponse register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);
}
