package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.entity.Admin;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.Token;
import com.hcmute.g2webstorev2.repository.TokenRepo;
import com.hcmute.g2webstorev2.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hcmute.g2webstorev2.enums.TokenType.BEARER;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    @Autowired
    private TokenRepo tokenRepo;
    @Override
    @Transactional
    public void saveUserToken(Customer customer, String token) {
        Token jwtToken = Token.builder()
                .token(token)
                .tokenType(BEARER)
                .customer(customer)
                .isMobile(false)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepo.save(jwtToken);
        log.info("Tokens saved successfully");
    }

    @Override
    @Transactional
    public void saveUserToken(Seller seller, String token) {
        Token jwtToken = Token.builder()
                .token(token)
                .tokenType(BEARER)
                .seller(seller)
                .isMobile(false)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepo.save(jwtToken);
        log.info("Tokens saved successfully");
    }

    @Override
    @Transactional
    public void saveUserToken(Admin admin, String token) {
        Token jwtToken = Token.builder()
                .token(token)
                .tokenType(BEARER)
                .admin(admin)
                .isMobile(false)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepo.save(jwtToken);
        log.info("Tokens saved successfully");
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Customer customer) {
        List<Token> tokens = tokenRepo.findAllValidTokenByCustomer(customer.getCustomerId());
        if (tokens.isEmpty()) return;

        tokenRepo.deleteAll(tokens);
        log.info("Tokens deleted successfully");
    }

    @Override
    public void revokeAllUserTokens(Seller seller) {
        List<Token> tokens = tokenRepo.findAllValidTokenBySeller(seller.getSellerId());
        if (tokens.isEmpty()) return;

        tokenRepo.deleteAll(tokens);

        log.info("Tokens deleted successfully");
    }

    @Override
    public void revokeAllUserTokens(Admin admin) {
        List<Token> tokens = tokenRepo.findAllValidTokenByAdmin(admin.getAdminId());
        if (tokens.isEmpty()) return;

        tokenRepo.deleteAll(tokens);
        log.info("Tokens deleted successfully");
    }
}
