package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.request.RefreshTokenRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import com.hcmute.g2webstorev2.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getInfo(){
        return ResponseEntity.ok(customerService.getInfo());
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest body){
        AuthResponse res = customerService.authenticate(body);
        return ResponseEntity
                .ok()
                .header("Access-Token", res.getAccessToken())
                .header("Refresh-Token", res.getRefreshToken())
                .body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody AuthRequest body){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.register(body));
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest body) throws IOException {
        return ResponseEntity.ok(customerService.refreshToken(body.getRefreshToken()));
    }
}
