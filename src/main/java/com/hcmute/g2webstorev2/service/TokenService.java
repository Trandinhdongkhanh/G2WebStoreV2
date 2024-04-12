package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.entity.Admin;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Seller;

public interface TokenService {
    void saveUserToken(Customer customer, String token);
    void saveUserToken(Seller seller, String token);
    void saveUserToken(Admin admin, String token);
    void revokeAllUserTokens(Customer customer);
    void revokeAllUserTokens(Seller seller);
    void revokeAllUserTokens(Admin admin);
}
