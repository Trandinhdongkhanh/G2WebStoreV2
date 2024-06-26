package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.ReviewRequest;
import com.hcmute.g2webstorev2.dto.response.ProductReviewsRes;
import com.hcmute.g2webstorev2.dto.response.ReviewResponse;
import com.hcmute.g2webstorev2.enums.ReviewSortType;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest body, MultipartFile[] files);

    ProductReviewsRes getReviewsByProduct(Integer id, Integer rating, ReviewSortType sortType, int pageNum, int pageSize);

    ReviewResponse addShopFeedBack(Integer reviewId, String shopFeedBack);

    Page<ReviewResponse> getUnFeedbackReviewsByShop(int page, int size, Integer star);
}
