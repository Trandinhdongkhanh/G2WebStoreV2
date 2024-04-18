package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.ReviewRequest;
import com.hcmute.g2webstorev2.dto.response.ReviewResponse;
import com.hcmute.g2webstorev2.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewService.createReview(body));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<List<ReviewResponse>> getReviewByProduct(
            @PathVariable("id") @NotNull(message = "Product ID must not be null")
            @Min(value = 1, message = "Product ID must not be less than 1") Integer id) {
        return ResponseEntity.ok(reviewService.getReviewByProduct(id));
    }
}
