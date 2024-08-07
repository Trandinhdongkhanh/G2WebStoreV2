package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.*;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.SellerResponse;
import com.hcmute.g2webstorev2.dto.response.SellersFromShopResponse;
import com.hcmute.g2webstorev2.service.SellerService;
import com.hcmute.g2webstorev2.util.CaptchaValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;
    private final CaptchaValidator captchaValidator;

    @GetMapping("/me")
    public ResponseEntity<SellerResponse> getInfo() {
        return ResponseEntity.ok(sellerService.getInfo());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest body) {
        AuthResponse res = sellerService.authenticate(body);
        return ResponseEntity
                .ok()
                .header("Acess-Token", res.getAccessToken())
                .header("Refresh-Token", res.getRefreshToken())
                .body(res);
    }

    @GetMapping("/activate-account")
    public ResponseEntity<String> activateAccount(@RequestParam("verification-code") String code) {
        sellerService.activateAccount(code);
        return ResponseEntity.ok("Account Activated");
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        sellerService.forgotPassword(email);
        return ResponseEntity.ok("Verification code has been sent to your email");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest body) {
        sellerService.resetPassword(body);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody AuthRequest body,
            @RequestParam(value = "g-recaptcha-response", required = false) String captcha) {
        if (captcha != null && captchaValidator.isValidCaptcha(captcha)) {
            sellerService.register(body);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Please check your email for verification code");
        }
        return ResponseEntity.badRequest().body("Please try again");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest body) throws IOException {
        return ResponseEntity.ok(sellerService.refreshToken(body.getRefreshToken()));
    }

    @PostMapping("/my-shop")
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS') or hasAuthority('CREATE_USER')")
    public ResponseEntity<SellerResponse> addSeller(@RequestBody @Valid SellerAddRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sellerService.addSeller(body));
    }

    @GetMapping("/my-shop")
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS') or hasAuthority('READ_USER')")
    public ResponseEntity<List<SellersFromShopResponse>> getSellersFromMyShop() {
        return ResponseEntity.ok(sellerService.getSellersFromMyShop());
    }

    @PutMapping("/upload-avatar")
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_FULL_ACCESS', " +
            "'SELLER_READ_ONLY'," +
            "'SELLER_ORDER_MANAGEMENT'," +
            "'JUNIOR_CHAT_AGENT'," +
            "'SELLER_PRODUCT_ACCESS'," +
            "'SELLER_PROMOTION_ACCESS')")
    public ResponseEntity<SellerResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(sellerService.uploadAvatar(file));
    }

    @PutMapping("/{sellerId}/role/{roleId}")
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS') or hasAuthority('UPDATE_USER')")
    public ResponseEntity<SellerResponse> updateSellerRole(
            @PathVariable("sellerId") Integer sellerId,
            @PathVariable("roleId") Integer roleId
    ) {
        return ResponseEntity.ok(sellerService.updateSellerRole(sellerId, roleId));
    }

    @PutMapping("/{sellerId}/enable")
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS') or hasAuthority('UPDATE_USER')")
    public ResponseEntity<SellerResponse> enableSeller(
            @PathVariable("sellerId") Integer sellerId,
            @RequestParam("isEnable") boolean isEnable) {
        return ResponseEntity.ok(sellerService.enableSeller(sellerId, isEnable));
    }

    @PutMapping("/me/phone-no")
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_FULL_ACCESS', " +
            "'SELLER_READ_ONLY'," +
            "'SELLER_ORDER_MANAGEMENT'," +
            "'JUNIOR_CHAT_AGENT'," +
            "'SELLER_PRODUCT_ACCESS'," +
            "'SELLER_PROMOTION_ACCESS')")
    public ResponseEntity<String> updatePhoneNo(@RequestBody @Valid PhoneNoUpdateRequest body) {
        sellerService.updatePhoneNo(body);
        return ResponseEntity.ok("Phone No updated successfully");
    }
    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_FULL_ACCESS', " +
            "'SELLER_READ_ONLY'," +
            "'SELLER_ORDER_MANAGEMENT'," +
            "'JUNIOR_CHAT_AGENT'," +
            "'SELLER_PRODUCT_ACCESS'," +
            "'SELLER_PROMOTION_ACCESS')")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid PasswordUpdateRequest body) {
        sellerService.updatePassword(body);
        return ResponseEntity.ok("Password updated successfully");
    }
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_FULL_ACCESS', " +
            "'SELLER_READ_ONLY'," +
            "'SELLER_ORDER_MANAGEMENT'," +
            "'JUNIOR_CHAT_AGENT'," +
            "'SELLER_PRODUCT_ACCESS'," +
            "'SELLER_PROMOTION_ACCESS')")
    public ResponseEntity<SellerResponse> updateProfile(
            @RequestParam("name") String name) {
        return ResponseEntity.ok(sellerService.updateProfile(name));
    }
}
