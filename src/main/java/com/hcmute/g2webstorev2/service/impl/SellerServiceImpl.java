package com.hcmute.g2webstorev2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.g2webstorev2.config.JwtService;
import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.repository.SellerRepo;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.SellerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.hcmute.g2webstorev2.enums.AppRole.SELLER_FULL_ACCESS;


@Service
@Slf4j
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private ShopRepo shopRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SellerResponse register(AuthRequest body) {
        Role role = roleRepo.findByAppRole(SELLER_FULL_ACCESS);
        if (role == null)
            throw new ResourceNotFoundException("Role SELLER_FULL_ACCESS not found");
        if (sellerRepo.existsByEmail(body.getEmail()))
            throw new ResourceNotUniqueException("Email existed");

        Shop shop = shopRepo.save(Shop.builder()
                .image(null)
                .name(body.getEmail())
                .build());

        Seller seller = new Seller();
        seller.setEmail(body.getEmail());
        seller.setPassword(passwordEncoder.encode(body.getPassword()));
        seller.setRole(role);
        seller.setEmailVerified(true);
        seller.setShop(shop);

        SellerResponse res = Mapper.toSellerResponse(sellerRepo.save(seller));

        log.info("Seller with id = " + res.getSellerId() + " created");
        return res;
    }

    @Override
    public AuthResponse authenticate(AuthRequest body) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        ":" + body.getEmail(),
                        body.getPassword()
                )
        );
        Seller seller = (Seller) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(seller);
        String refreshToken = jwtService.generateRefreshToken(seller);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String refreshToken = authHeader.substring(7);
        if (!jwtService.isTokenValid(refreshToken)) return;

        String email = jwtService.extractEmail(refreshToken);

        if (email != null) {
            Seller seller = sellerRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Email '" + email + "' not existed"));
            String accessToken = jwtService.generateAccessToken(seller);

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            new ObjectMapper().writeValue(res.getOutputStream(), authResponse);
        }
    }

    @Override
    public SellerResponse getInfo() {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (seller == null) throw new UsernameNotFoundException("Please login");

        return Mapper.toSellerResponse(seller);
    }
}
