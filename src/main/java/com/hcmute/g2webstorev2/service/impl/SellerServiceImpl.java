package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.config.JwtService;
import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.request.ResetPasswordRequest;
import com.hcmute.g2webstorev2.dto.request.SellerAddRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import com.hcmute.g2webstorev2.dto.response.SellersFromShopResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.exception.AccountNotActivatedException;
import com.hcmute.g2webstorev2.exception.OTPExpiredException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.OTPRepo;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.repository.SellerRepo;
import com.hcmute.g2webstorev2.repository.ShopRepo;
import com.hcmute.g2webstorev2.service.EmailService;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.SellerService;
import com.hcmute.g2webstorev2.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private TokenService tokenService;
    @Autowired
    private FileService fileService;
    @Autowired
    private OTPRepo otpRepo;
    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public void register(AuthRequest body) {
        Role role = roleRepo.findByAppRole(SELLER_FULL_ACCESS);
        if (role == null)
            throw new ResourceNotFoundException("Role SELLER_FULL_ACCESS not found");
        if (sellerRepo.existsByEmail(body.getEmail()))
            throw new ResourceNotUniqueException("Email existed");

        Shop shop = shopRepo.save(Shop.builder()
                .image(null)
                .name(body.getEmail())
                .build());

        log.info("Shop with ID = " + shop.getShopId() + " created successfully");

        Seller seller = new Seller();
        seller.setEmail(body.getEmail());
        seller.setPassword(passwordEncoder.encode(body.getPassword()));
        seller.setRole(role);
        seller.setEmailVerified(false);
        seller.setMainAcc(true);
        seller.setShop(shop);

        Seller res = sellerRepo.save(seller);
        log.info("Seller with ID = " + res.getSellerId() + " registered successfully");

        String verificationCode = generateAndSaveActivationToken(seller);
        emailService.sendVerificationCode(verificationCode, seller.getEmail(), "Activation Account");
    }

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest body) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        ":" + body.getEmail(),
                        body.getPassword()
                )
        );
        Seller seller = (Seller) authentication.getPrincipal();

        if (!seller.isEmailVerified()) {
            String verificationCode = this.generateAndSaveActivationToken(seller);
            emailService.sendVerificationCode(verificationCode, seller.getEmail(), "Activate Account");
            throw new AccountNotActivatedException("Your account has not been activated, " +
                    "please check your email to retrieve the activation code");
        }

        String accessToken = jwtService.generateAccessToken(seller);
        String refreshToken = jwtService.generateRefreshToken(seller);

        tokenService.revokeAllUserTokens(seller);
        tokenService.saveUserToken(seller, accessToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) return null;

        String email = jwtService.extractEmail(refreshToken);

        if (email != null) {
            Seller seller = sellerRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Email '" + email + "' not existed"));
            String accessToken = jwtService.generateAccessToken(seller);

            tokenService.revokeAllUserTokens(seller);
            tokenService.saveUserToken(seller, accessToken);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        return null;
    }

    @Override
    public SellerResponse getInfo() {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (seller == null) throw new UsernameNotFoundException("Please login");

        return Mapper.toSellerResponse(seller);
    }

    @Override
    @Transactional
    public SellerResponse addSeller(SellerAddRequest body) {
        if (sellerRepo.existsByEmail(body.getEmail()))
            throw new ResourceNotUniqueException("Seller with email = '" + body.getEmail() + "' already existed");

        Role role = roleRepo.findById(body.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID = " + body.getRoleId() + " not found"));

        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SellerResponse res = Mapper.toSellerResponse(sellerRepo.save(Seller.builder()
                .email(body.getEmail())
                .password(passwordEncoder.encode(body.getPassword()))
                .role(role)
                .shop(seller.getShop())
                .isMainAcc(false)
                .isEmailVerified(false)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isCredentialsNonExpired(true)
                .isAccountNonLocked(true)
                .build()));

        log.info("Seller with ID = " + res.getSellerId() + " added to shop with ID = " + seller.getShop().getShopId()
                + " successfully");

        return res;
    }

    @Override
    public List<SellersFromShopResponse> getSellersFromMyShop() {
        Seller adminSeller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return sellerRepo.findAllByShop(adminSeller.getShop())
                .stream().map(Mapper::toSellersFromShopResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SellerResponse uploadAvatar(MultipartFile file) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (seller.getAvatar() != null) fileService.delFile(seller.getAvatar().getId());

        List<GCPFile> images = fileService.uploadFiles(new MultipartFile[]{file});
        seller.setAvatar(images.get(0));
        images.get(0).setSeller(seller);

        SellerResponse res = Mapper.toSellerResponse(sellerRepo.save(seller));
        log.info("Seller with ID = " + seller.getSellerId() + " updated avatar successfully");
        return res;
    }

    @Override
    public SellerResponse updateSellerRole(Integer sellerId, Integer roleId) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID = " + roleId + " not found"));

        Seller seller = sellerRepo.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller with ID = " + sellerId + " not found"));


        seller.setRole(role);
        SellerResponse res = Mapper.toSellerResponse(sellerRepo.save(seller));
        log.info("Seller with ID = " + sellerId + " has been updated to role " + role.getAppRole().name());
        return res;
    }

    @Override
    @Transactional
    public void activateAccount(String verificationCode) {
        OTP otp = otpRepo.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid OTP"));

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            emailService.sendVerificationCode(
                    generateAndSaveActivationToken(otp.getSeller()),
                    otp.getSeller().getEmail(),
                    "Activate Account"
            );
            throw new OTPExpiredException("OTP is expired. A new OTP has been sent to your email");
        }

        otp.getSeller().setEmailVerified(true);
        Seller seller = sellerRepo.save(otp.getSeller());

        otpRepo.deleteAllBySellerId(seller.getSellerId());
    }

    @Override
    public void forgotPassword(String email) {
        Seller seller = sellerRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        String verificationCode = generateAndSaveActivationToken(seller);
        emailService.sendVerificationCode(verificationCode, seller.getEmail(), "Reset Password");
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest body) {
        OTP otp = otpRepo.findByVerificationCode(body.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid OTP"));

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            emailService.sendVerificationCode(
                    generateAndSaveActivationToken(otp.getSeller()),
                    otp.getSeller().getEmail(),
                    "Reset Password"
            );
            throw new OTPExpiredException("OTP is expired. A new OTP has been sent to your email");
        }

        otp.getSeller().setPassword(passwordEncoder.encode(body.getNewPass()));
        Seller seller = sellerRepo.save(otp.getSeller());

        otpRepo.deleteAllBySellerId(seller.getSellerId());
    }

    private String generateAndSaveActivationToken(Seller seller) {
        String verificationCode = RandomStringUtils.random(6, "0123456789");
        OTP otp = OTP.builder()
                .verificationCode(verificationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .seller(seller)
                .build();

        otpRepo.save(otp);
        log.info("Verification code saved to customer " + seller.getEmail());
        return verificationCode;
    }
}
