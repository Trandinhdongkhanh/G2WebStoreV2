package com.hcmute.g2webstorev2.exception;

public class GCSFileNotFoundException extends RuntimeException{
    public GCSFileNotFoundException(String message) {
        super(message);
    }
}
