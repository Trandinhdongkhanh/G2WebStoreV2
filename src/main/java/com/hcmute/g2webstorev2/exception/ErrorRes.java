package com.hcmute.g2webstorev2.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorRes {
    private int code;
    private HttpStatus status;
    private String message;
    private LocalDateTime timestamp;
}
