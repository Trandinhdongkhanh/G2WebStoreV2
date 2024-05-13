package com.hcmute.g2webstorev2.service.impl;

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
import com.hcmute.g2webstorev2.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hcmute.g2webstorev2.enums.AppRole.ADMIN;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepo adminRepo;
    private final RoleRepo roleRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    @Transactional
    public AuthResponse register(AuthRequest body) {
        if (adminRepo.existsByEmail(body.getEmail()))
            throw new ResourceNotUniqueException("Email existed");
        Role role = roleRepo.findByAppRole(ADMIN);
        if (role == null)
            throw new ResourceNotFoundException("Role " + ADMIN.name() + " not existed in database");

        Admin admin = new Admin();
        admin.setEmail(body.getEmail());
        admin.setPassword(passwordEncoder.encode(body.getPassword()));
        admin.setRole(role);

        Admin res = adminRepo.save(admin);
        log.info("Admin with ID = " + res.getAdminId() + " created successfully");

        String accessToken = jwtService.generateAccessToken(admin);
        String refreshToken = jwtService.generateRefreshToken(admin);

        tokenService.saveUserToken(admin, accessToken);
        return new AuthResponse(accessToken, refreshToken);
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

        tokenService.revokeAllUserTokens(admin);
        tokenService.saveUserToken(admin, accessToken);


        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) return null;

        String email = jwtService.extractEmail(refreshToken);

        if (email != null) {
            Admin admin = adminRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Email '" + email + "' not existed"));
            String accessToken = jwtService.generateAccessToken(admin);

            tokenService.revokeAllUserTokens(admin);
            tokenService.saveUserToken(admin, accessToken);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        return null;
    }

    @Override
    public AdminResponse getInfo() {
        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (admin == null) throw new UsernameNotFoundException("Please login");

        return Mapper.toAdminResponse(admin);
    }
}
