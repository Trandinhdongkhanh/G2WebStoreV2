package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.ReviewRequest;
import com.hcmute.g2webstorev2.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest body);
    List<ReviewResponse> getReviewByProduct(Integer id);
}
