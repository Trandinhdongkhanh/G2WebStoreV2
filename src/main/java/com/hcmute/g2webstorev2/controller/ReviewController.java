package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.ReviewRequest;
import com.hcmute.g2webstorev2.dto.response.ProductReviewsRes;
import com.hcmute.g2webstorev2.dto.response.ReviewResponse;
import com.hcmute.g2webstorev2.enums.ReviewSortType;
import com.hcmute.g2webstorev2.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER') or hasAnyAuthority('CREATE_REVIEW')")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @ModelAttribute ReviewRequest body,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewService.createReview(body, files));
    }

    @PutMapping("/{reviewId}/shop-feedback")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'JUNIOR_CHAT_AGENT') or hasAnyAuthority('UPDATE_REVIEW')")
    public ResponseEntity<ReviewResponse> updateShopFeedBack(
            @PathVariable("reviewId") Integer reviewId,
            @RequestParam("feedBack") String feedBack
    ) {
        return ResponseEntity.ok(reviewService.addShopFeedBack(reviewId, feedBack));
    }
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductReviewsRes> getReviewsByProduct(
            @PathVariable("id") @NotNull(message = "Product ID must not be null")
            @Min(value = 1, message = "Product ID must not be less than 1") Integer id,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "sortType", required = false) ReviewSortType sortType,
            @RequestParam(value = "pageNum") int pageNum,
            @RequestParam(value = "pageSize") int pageSize) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(id, rating, sortType, pageNum, pageSize));
    }
}
