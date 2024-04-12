package com.hcmute.g2webstorev2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.g2webstorev2.entity.Token;
import com.hcmute.g2webstorev2.repository.TokenRepo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TokenRepo tokenRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        //Check if the access token existed in the database
        //If we bypass this then 100% our access token is true and does exist in database
        String accessToken = authHeader.substring(7);
        Optional<Token> optToken = tokenRepo.findByToken(accessToken);
        if (optToken.isEmpty()) {
            sendError(response, "Token invalid");
            return;
        }

        //Now we need to check the expiration,... of the token, if the token is expired we delete it in the database
        try {
            String email = jwtService.extractEmail(accessToken);
            String role = jwtService.extractRole(accessToken);
            if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(role + ":" + email);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            }
        } catch (ExpiredJwtException ex) {
            sendError(response, ex.getMessage());
            tokenRepo.delete(optToken.get());
            log.info("Deleted token successfully");
        } catch (IllegalArgumentException | MalformedJwtException | UnsupportedJwtException ex) {
            sendError(response, ex.getMessage());
        }
    }

    private void sendError(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> err = new HashMap<>();
        err.put("code", HttpStatus.UNAUTHORIZED.value());
        err.put("status", HttpStatus.UNAUTHORIZED);
        err.put("message", message);
        err.put("timestamp", LocalDateTime.now());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.writeValue(res.getOutputStream(), err);

        log.error(message);
    }
}
