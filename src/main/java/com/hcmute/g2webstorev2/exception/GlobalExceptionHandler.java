package com.hcmute.g2webstorev2.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRes> handleServerException(Exception e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(err);
    }
    @ExceptionHandler(VoucherException.class)
    public ResponseEntity<ErrorRes> handleVoucherException(VoucherException e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(err);
    }
    @ExceptionHandler(ShopCategoryException.class)
    public ResponseEntity<ErrorRes> handleShopCategoryException(ShopCategoryException e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(err);
    }
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorRes> handleJwtException(JwtException e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorRes> handleAccessDeniedException(AccessDeniedException e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }
    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ErrorRes> handleAccountStatusException(AccountStatusException e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }
    @ExceptionHandler(ResourceNotUniqueException.class)
    public ResponseEntity<ErrorRes> handleResourceNotUniqueException(ResourceNotUniqueException e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(err);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorRes> handleBadCredentialsException(BadCredentialsException e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(err);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorRes> handleResourceNotFoundException(ResourceNotFoundException e){
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(err);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRes> handleValidationException(MethodArgumentNotValidException e){
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorRes err = ErrorRes.builder()
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(message);
        return ResponseEntity.badRequest().body(err);
    }
}
