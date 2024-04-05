package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.response.AdminResponse;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import com.hcmute.g2webstorev2.service.AdminService;
import com.hcmute.g2webstorev2.service.CustomerService;
import com.hcmute.g2webstorev2.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private SellerService sellerService;
    @PostMapping("/customer/login")
    public ResponseEntity<AuthResponse> customerLogin(@RequestBody @Valid AuthRequest body){
        AuthResponse res = customerService.authenticate(body);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, res.getAccessToken())
                .header("Refresh-Token", res.getRefreshToken())
                .body(res);
    }
    @PostMapping("/seller/login")
    public ResponseEntity<AuthResponse> sellerLogin(@RequestBody @Valid AuthRequest body){
        AuthResponse res = sellerService.authenticate(body);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, res.getAccessToken())
                .header("Refresh-Token", res.getRefreshToken())
                .body(res);
    }
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@RequestBody @Valid AuthRequest body){
        AuthResponse res = adminService.authenticate(body);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, res.getAccessToken())
                .header("Refresh-Token", res.getRefreshToken())
                .body(res);
    }
    @PostMapping("/customer/register")
    public ResponseEntity<CustomerResponse> customerRegister(@RequestBody @Valid AuthRequest body){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/customer/register").toUriString());
        return ResponseEntity
                .created(uri)
                .body(customerService.register(body));
    }
    @PostMapping("/seller/register")
    public ResponseEntity<SellerResponse> sellerRegister(@RequestBody @Valid AuthRequest body){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/seller/register").toUriString());
        return ResponseEntity
                .created(uri)
                .body(sellerService.register(body));
    }
    @PostMapping("/admin/register")
    public ResponseEntity<AdminResponse> adminRegister(@RequestBody @Valid AuthRequest body){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/admin/register").toUriString());
        return ResponseEntity
                .created(uri)
                .body(adminService.register(body));
    }
}
