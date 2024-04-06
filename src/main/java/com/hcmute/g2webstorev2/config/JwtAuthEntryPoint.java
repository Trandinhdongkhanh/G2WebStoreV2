package com.hcmute.g2webstorev2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.g2webstorev2.exception.ErrorRes;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//        ErrorRes err = ErrorRes.builder()
//                .code(HttpStatus.UNAUTHORIZED.value())
//                .status(HttpStatus.UNAUTHORIZED)
//                .message(authException.getMessage())
//                .build();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        response.getWriter().write(objectMapper.writeValueAsString(err));
//        response.flushBuffer();

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
