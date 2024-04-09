package com.hcmute.g2webstorev2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.g2webstorev2.config.JwtService;
import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CustomerRepo;
import com.hcmute.g2webstorev2.repository.RoleRepo;
import com.hcmute.g2webstorev2.service.CustomerService;
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

    @Override
    @Transactional
    public CustomerResponse register(AuthRequest body) {
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

        CustomerResponse res = Mapper.toCustomerResponse(customerRepo.save(customer));
        log.info("Customer with id " + res.getCustomerId() + " created");
        return res;
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
            Customer customer = customerRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Email '" + email + "' not existed"));
            String accessToken = jwtService.generateAccessToken(customer);

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            new ObjectMapper().writeValue(res.getOutputStream(), authResponse);
        }
    }

    @Override
    public CustomerResponse getInfo() {
        return null;
    }
}
