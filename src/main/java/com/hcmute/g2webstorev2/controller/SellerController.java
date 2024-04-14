package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.AuthRequest;
import com.hcmute.g2webstorev2.dto.request.RefreshTokenRequest;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import com.hcmute.g2webstorev2.service.SellerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {
    @Autowired
    private SellerService sellerService;
    @GetMapping("/me")
    public ResponseEntity<SellerResponse> getInfo(){
        return ResponseEntity.ok(sellerService.getInfo());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest body){
        AuthResponse res = sellerService.authenticate(body);
        return ResponseEntity
                .ok()
                .header("Acess-Token", res.getAccessToken())
                .header("Refresh-Token", res.getRefreshToken())
                .body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest body){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sellerService.register(body));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest body) throws IOException {
        return ResponseEntity.ok(sellerService.refreshToken(body.getRefreshToken()));
    }
}
