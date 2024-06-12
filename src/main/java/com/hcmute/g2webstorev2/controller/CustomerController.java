package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.*;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import com.hcmute.g2webstorev2.service.CustomerService;
import com.hcmute.g2webstorev2.util.CaptchaValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CaptchaValidator captchaValidator;

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getInfo() {
        return ResponseEntity.ok(customerService.getInfo());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest body) {
        AuthResponse res = customerService.authenticate(body);
        return ResponseEntity
                .ok()
                .header("Access-Token", res.getAccessToken())
                .header("Refresh-Token", res.getRefreshToken())
                .body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody AuthRequest body,
            @RequestParam(value = "g-recaptcha-response", required = false) String captcha) {
        if (captcha != null && captchaValidator.isValidCaptcha(captcha)) {
            customerService.register(body);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Please check your email for verification code");
        }
        return ResponseEntity.badRequest().body("Please try again");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest body) throws IOException {
        return ResponseEntity.ok(customerService.refreshToken(body.getRefreshToken()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> updateProfile(
            @RequestBody @Valid CustomerProfileUpdateRequest body) {
        return ResponseEntity.ok(customerService.updateProfile(body));
    }

    @PutMapping("/me/upload-avatar")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(customerService.uploadAvatar(file));
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        customerService.forgotPassword(email);
        return ResponseEntity.ok("Verification code has been sent to your email");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest body) {
        customerService.resetPassword(body);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/activate-account")
    public ResponseEntity<String> activateAccount(@RequestParam("verification-code") String code) {
        customerService.activateAccount(code);
        return ResponseEntity.ok("Account activated");
    }

    @PutMapping("/me/password")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid PasswordUpdateRequest body) {
        customerService.updatePassword(body);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PutMapping("/me/email")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> updateEmail(@RequestBody @Valid EmailUpdateRequest body) {
        customerService.updateEmail(body);
        return ResponseEntity.ok("Email updated successfully");
    }

    @PutMapping("/me/phone-no")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> updatePhoneNo(@RequestBody @Valid PhoneNoUpdateRequest body) {
        customerService.updatePhoneNo(body);
        return ResponseEntity.ok("Phone No updated successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CustomerResponse>> getCustomers(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "size") int size
    ) {
        return ResponseEntity.ok(customerService.getCustomers(page, size));
    }

    @PutMapping("/{id}/locked")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> lockedCustomer(
            @RequestParam(defaultValue = "true", name = "isLocked") boolean isLocked,
            @PathVariable("id") Integer customerId) {
        return ResponseEntity.ok(customerService.lockCustomer(customerId, isLocked));
    }
}
