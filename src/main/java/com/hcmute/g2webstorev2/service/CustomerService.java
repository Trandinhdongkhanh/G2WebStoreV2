package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);
}
