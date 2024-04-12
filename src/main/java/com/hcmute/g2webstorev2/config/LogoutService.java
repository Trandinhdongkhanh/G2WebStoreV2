package com.hcmute.g2webstorev2.config;

import com.hcmute.g2webstorev2.entity.Token;
import com.hcmute.g2webstorev2.repository.TokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LogoutService implements LogoutHandler {
    @Autowired
    private TokenRepo tokenRepo;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        String token = authHeader.substring(7);
        Optional<Token> storedToken = tokenRepo.findByToken(token);
        storedToken.ifPresent(value -> tokenRepo.delete(value));
    }
}
