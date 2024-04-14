package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.config.JwtService;
import com.hcmute.g2webstorev2.dto.request.*;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.exception.*;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CustomerRepo;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.service.CustomerService;
import com.hcmute.g2webstorev2.service.TokenService;
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


import java.util.Objects;

import static com.hcmute.g2webstorev2.enums.AppRole.*;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private TokenService tokenService;

    @Override
    @Transactional
    public AuthResponse register(AuthRequest body) {
        if (customerRepo.existsByEmail(body.getEmail()))
            throw new ResourceNotUniqueException("Email existed");
        Role role = roleRepo.findByAppRole(CUSTOMER);
        if (role == null)
            throw new ResourceNotFoundException("Role " + CUSTOMER.name() + " not existed in database");

        Customer customer = new Customer();
        customer.setEmail(body.getEmail());
        customer.setPassword(passwordEncoder.encode(body.getPassword()));
        customer.setEmailVerified(true);
        customer.setRole(role);

        Customer res = customerRepo.save(customer);
        log.info("Customer with ID = " + res.getCustomerId() + " has been registered successfully");

        String accessToken = jwtService.generateAccessToken(res);
        String refreshToken = jwtService.generateRefreshToken(res);

        tokenService.saveUserToken(res, accessToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
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

        customer.setAvatar(body.getAvatar());
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
}
