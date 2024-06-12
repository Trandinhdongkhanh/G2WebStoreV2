package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.*;
import com.hcmute.g2webstorev2.dto.response.AuthResponse;
import com.hcmute.g2webstorev2.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


public interface CustomerService {
    void register(AuthRequest body);

    AuthResponse authenticate(AuthRequest body);

    AuthResponse refreshToken(String refreshToken);

    CustomerResponse getInfo();

    CustomerResponse updateProfile(CustomerProfileUpdateRequest body);

    void updatePassword(PasswordUpdateRequest body);

    void updateEmail(EmailUpdateRequest body);

    void updatePhoneNo(PhoneNoUpdateRequest body);

    CustomerResponse uploadAvatar(MultipartFile file);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest body);

    void activateAccount(String verificationCode);

    Page<CustomerResponse> getCustomers(int page, int size);

    CustomerResponse lockCustomer(Integer customerId, boolean isLocked);
}
