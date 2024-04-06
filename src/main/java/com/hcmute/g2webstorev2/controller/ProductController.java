package com.hcmute.g2webstorev2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    @GetMapping("/")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok("Hello world");
    }
}
