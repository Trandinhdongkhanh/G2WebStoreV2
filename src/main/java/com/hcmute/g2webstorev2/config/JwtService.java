package com.hcmute.g2webstorev2.config;

import com.hcmute.g2webstorev2.entity.Admin;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Seller;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email")
                .toString();
    }
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role")
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

    public String generateRefreshToken(Customer customer) {
        return Jwts.builder()
                .setIssuer("https://g2store.vn")
                .setSubject(customer.getCustomerId().toString())
                .claim("email", customer.getEmail())
                .claim("role", customer.getRole().getAppRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Seller seller) {
        return Jwts.builder()
                .setIssuer("https://g2store.vn")
                .setSubject(seller.getSellerId().toString())
                .claim("email", seller.getEmail())
                .claim("role", seller.getRole().getAppRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Admin admin) {
        return Jwts.builder()
                .setIssuer("https://g2store.vn")
                .setSubject(admin.getAdminId().toString())
                .claim("email", admin.getEmail())
                .claim("role", admin.getRole().getAppRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            throw e;
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
