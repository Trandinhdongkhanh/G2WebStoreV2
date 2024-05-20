package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.config.JwtService;
import com.hcmute.g2webstorev2.dto.request.*;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.exception.*;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CustomerRepo;
import com.hcmute.g2webstorev2.repository.OTPRepo;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.service.CustomerService;
import com.hcmute.g2webstorev2.service.EmailService;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.Objects;

import static com.hcmute.g2webstorev2.enums.AppRole.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final RoleRepo roleRepo;
    private final TokenService tokenService;
    private final FileService fileService;
    private final OTPRepo otpRepo;
    private final EmailService emailService;

    @Override
    @Transactional
    public void register(AuthRequest body) {
        if (customerRepo.existsByEmail(body.getEmail()))
            throw new ResourceNotUniqueException("Email existed");
        Role role = roleRepo.findByAppRole(CUSTOMER);
        if (role == null)
            throw new ResourceNotFoundException("Role " + CUSTOMER.name() + " not existed in database");

        Customer customer = new Customer();
        customer.setEmail(body.getEmail());
        customer.setPassword(passwordEncoder.encode(body.getPassword()));
        customer.setEmailVerified(false);
        customer.setRole(role);

        Customer res = customerRepo.save(customer);
        log.info("Customer with ID = " + res.getCustomerId() + " has been registered successfully");

        String verificationCode = generateAndSaveActivationToken(customer);
        emailService.sendVerificationCode(verificationCode, customer.getEmail(), "Activate Account");
    }

    @Override
    @Transactional(noRollbackFor = AccountNotActivatedException.class)
    public AuthResponse authenticate(AuthRequest body) {
        //The authentication manager will search for AuthenticationProvider which we define in the SecurityConfig file
        //The AuthenticationProvider we define uses DaoAuthenticationProvider which use the CustomUserDetailsService
        //The CustomUsersDetailsService override the loadByUserName method and will be called, so we have to pass in
        // the role prefix as below to determine which repository we will use (see CustomUserDetailsService file for more detail)
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        CUSTOMER.name() + ":" + body.getEmail(),
                        body.getPassword()
                )
        );
        Customer customer = (Customer) authentication.getPrincipal();

        if (!customer.isEmailVerified()) {
            String verificationCode = this.generateAndSaveActivationToken(customer);
            emailService.sendVerificationCode(verificationCode, customer.getEmail(), "Activate Account");
            throw new AccountNotActivatedException("Your account has not been activated, " +
                    "please check your email to retrieve the activation code");
        }

        String accessToken = jwtService.generateAccessToken(customer);
        String refreshToken = jwtService.generateRefreshToken(customer);

        tokenService.revokeAllUserTokens(customer);
        tokenService.saveUserToken(customer, accessToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) return null;

        String email = jwtService.extractEmail(refreshToken);

        if (email != null) {
            Customer customer = customerRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Email '" + email + "' not existed"));
            String accessToken = jwtService.generateAccessToken(customer);

            tokenService.revokeAllUserTokens(customer);
            tokenService.saveUserToken(customer, accessToken);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        return null;
    }

    @Override
    public CustomerResponse getInfo() {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (customer == null) throw new UsernameNotFoundException("Please login");

        return Mapper.toCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateProfile(CustomerProfileUpdateRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        customer.setDob(body.getDob());
        customer.setFullName(body.getFullName());

        CustomerResponse res = Mapper.toCustomerResponse(customerRepo.save(customer));
        log.info("Update customer with ID = " + customer.getCustomerId() + " successfully");
        return res;
    }

    @Override
    @Transactional
    public void updatePassword(PasswordUpdateRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!passwordEncoder.matches(body.getOldPassword(), customer.getPassword()))
            throw new PasswordNotMatchException("Incorrect old password");

        customer.setPassword(passwordEncoder.encode(body.getNewPassword()));
        customerRepo.save(customer);
        log.info("Updated password of customer with ID = " + customer.getCustomerId() + " successfully");
    }

    @Override
    @Transactional
    public void updateEmail(EmailUpdateRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (Objects.equals(customer.getEmail(), body.getNewEmail()))
            throw new EmailException("New email must be different from current email");

        if (customerRepo.existsByEmail(body.getNewEmail()))
            throw new ResourceNotUniqueException("Email existed");

        customer.setEmail(body.getNewEmail());
        customerRepo.save(customer);
        log.info("Email of customer with ID = " + customer.getCustomerId() + " updated successfully");
    }

    @Override
    public void updatePhoneNo(PhoneNoUpdateRequest body) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (Objects.equals(customer.getPhoneNo(), body.getNewPhoneNo()))
            throw new PhoneNoException("New Phone No must be different from current Phone No");

        if (customerRepo.existsByPhoneNo(body.getNewPhoneNo()))
            throw new ResourceNotUniqueException("Phone No existed");

        customer.setPhoneNo(body.getNewPhoneNo());
        customerRepo.save(customer);
        log.info("Phone No of customer with ID = " + customer.getCustomerId() + " updated successfully");
    }

    @Override
    @Transactional
    public CustomerResponse uploadAvatar(MultipartFile file) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (customer.getAvatar() != null) fileService.delFile(customer.getAvatar().getId());

        List<GCPFile> images = fileService.uploadFiles(new MultipartFile[]{file});
        customer.setAvatar(images.get(0));
        images.get(0).setCustomer(customer);

        customerRepo.save(customer);
        log.info("Customer with ID = " + customer.getCustomerId() + " updated avatar successfully");

        return Mapper.toCustomerResponse(customer);
    }

    @Override
    public void forgotPassword(String email) {
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        String verificationCode = generateAndSaveActivationToken(customer);
        emailService.sendVerificationCode(verificationCode, email, "Reset Password");
    }

    @Override
    @Transactional(noRollbackFor = OTPExpiredException.class)
    public void resetPassword(ResetPasswordRequest body) {
        OTP otp = otpRepo.findByVerificationCode(body.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid OTP"));

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            emailService.sendVerificationCode(
                    generateAndSaveActivationToken(otp.getCustomer()),
                    otp.getCustomer().getEmail(),
                    "Reset Password"
            );
            throw new OTPExpiredException("OTP is expired. A new OTP has been sent to your email");
        }

        otp.getCustomer().setPassword(passwordEncoder.encode(body.getNewPass()));
        Customer customer = customerRepo.save(otp.getCustomer());

        otpRepo.deleteAllByCustomerId(customer.getCustomerId());
    }

    @Override
    @Transactional(noRollbackFor = OTPExpiredException.class)
    public void activateAccount(String verificationCode) {
        OTP otp = otpRepo.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid OTP"));

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            emailService.sendVerificationCode(
                    generateAndSaveActivationToken(otp.getCustomer()),
                    otp.getCustomer().getEmail(),
                    "Activate Account"
            );
            throw new OTPExpiredException("OTP is expired. A new OTP has been sent to your email");
        }

        otp.getCustomer().setEmailVerified(true);
        Customer customer = customerRepo.save(otp.getCustomer());

        otpRepo.deleteAllByCustomerId(customer.getCustomerId());
    }

    private String generateAndSaveActivationToken(Customer customer) {
        String verificationCode = RandomStringUtils.random(6, "0123456789");
        OTP otp = OTP.builder()
                .verificationCode(verificationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .customer(customer)
                .build();

        otpRepo.save(otp);
        log.info("Verification code saved to customer " + customer.getEmail());
        return verificationCode;
    }
}
