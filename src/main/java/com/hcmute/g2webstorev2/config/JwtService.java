package com.hcmute.g2webstorev2.config;

import com.hcmute.g2webstorev2.entity.Admin;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Seller;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractEmail(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("email")
                .toString();
    }

    public String extractUserId(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }
    public String extractRole(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("role")
                .toString();
    }

    public String extractShopId(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("shop")
                .toString();
    }

    public String generateAccessToken(Customer customer) {
        return Jwts.builder()
                .setIssuer("https://g2store.vn")
                .setSubject(customer.getCustomerId().toString())
                .claim("email", customer.getEmail())
                .claim("role", customer.getRole().getAppRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Seller seller) {
        return Jwts.builder()
                .setIssuer("https://g2store.vn")
                .setSubject(seller.getSellerId().toString())
                .claim("email", seller.getEmail())
                .claim("shop", seller.getShop().getShopId())
                .claim("role", seller.getRole().getAppRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Admin admin) {
        return Jwts.builder()
                .setIssuer("https://g2store.vn")
                .setSubject(admin.getAdminId().toString())
                .claim("email", admin.getEmail())
                .claim("role", admin.getRole().getAppRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
