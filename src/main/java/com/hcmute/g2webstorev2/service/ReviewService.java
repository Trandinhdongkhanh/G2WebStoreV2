package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.ReviewRequest;
import com.hcmute.g2webstorev2.dto.response.ReviewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest body, MultipartFile[] files);
    List<ReviewResponse> getReviewByProduct(Integer id);
    ReviewResponse addShopFeedBack(Integer reviewId, String shopFeedBack);
}
