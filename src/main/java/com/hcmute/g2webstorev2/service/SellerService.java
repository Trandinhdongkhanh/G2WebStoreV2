package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import org.springframework.stereotype.Service;

public interface SellerService {
    SellerResponse register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);
}
