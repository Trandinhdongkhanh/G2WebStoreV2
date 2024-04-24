package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.request.SellerAddRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import com.hcmute.g2webstorev2.dto.response.SellersFromShopResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface SellerService {
    AuthResponse register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);

    AuthResponse refreshToken (String refreshToken);

    SellerResponse getInfo();
    SellerResponse addSeller(SellerAddRequest body);

    List<SellersFromShopResponse> getSellersFromMyShop();
    SellerResponse uploadAvatar(MultipartFile file);
}
