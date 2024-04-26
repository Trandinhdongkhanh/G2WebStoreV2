package com.hcmute.g2webstorev2.exception;

public class OTPExpiredException extends RuntimeException{
    public OTPExpiredException(String message) {
        super(message);
    }
}
