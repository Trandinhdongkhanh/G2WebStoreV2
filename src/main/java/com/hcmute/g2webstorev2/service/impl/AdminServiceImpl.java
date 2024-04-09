package com.hcmute.g2webstorev2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.g2webstorev2.config.JwtService;
import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AdminResponse;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.entity.Admin;
import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.AdminRepo;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.service.AdminService;
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

import static com.hcmute.g2webstorev2.enums.AppRole.ADMIN;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepo adminRepo;
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
    public AdminResponse register(AuthRequest body) {
        if (adminRepo.existsByEmail(body.getEmail()))
            throw new ResourceNotUniqueException("Email existed");
        Role role = roleRepo.findByAppRole(ADMIN);
        if (role == null)
            throw new ResourceNotFoundException("Role " + ADMIN.name() + " not existed in database");

        Admin admin = new Admin();
        admin.setEmail(body.getEmail());
        admin.setPassword(passwordEncoder.encode(body.getPassword()));
        admin.setRole(role);

        AdminResponse res = Mapper.toAdminResponse(adminRepo.save(admin));
        log.info("Admin created successfully");
        return res;
    }

    @Override
    public AuthResponse authenticate(AuthRequest body) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        "ADMIN:" + body.getEmail(),
                        body.getPassword()
                )
        );
        Admin admin = (Admin) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(admin);
        String refreshToken = jwtService.generateRefreshToken(admin);
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
            Admin admin = adminRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Email '" + email + "' not existed"));
            String accessToken = jwtService.generateAccessToken(admin);

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            new ObjectMapper().writeValue(res.getOutputStream(), authResponse);
        }
    }

    @Override
    public AdminResponse getInfo() {
        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (admin == null) throw new UsernameNotFoundException("Please login");

        return Mapper.toAdminResponse(admin);
    }
}
