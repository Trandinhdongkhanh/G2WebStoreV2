package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ReviewRequest;
import com.hcmute.g2webstorev2.dto.response.ProductReviewsRes;
import com.hcmute.g2webstorev2.dto.response.ReviewResponse;
import com.hcmute.g2webstorev2.entity.*;
import com.hcmute.g2webstorev2.enums.ReviewSortType;
import com.hcmute.g2webstorev2.exception.ProductReviewedException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.OrderItemRepo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.ReviewRepo;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepo reviewRepo;
    private final ProductRepo productRepo;
    private final FileService fileService;
    private final OrderItemRepo orderItemRepo;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest body, MultipartFile[] files) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OrderItem orderItem = orderItemRepo.findById(body.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order Item not found"));
        Product product = productRepo.findById(orderItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Can't create review, product not found"));

        if (orderItem.isReviewed())
            throw new ProductReviewedException("Product reviewed, can't create new review for this product");

        Review review = Review.builder()
                .content(body.getContent())
                .rate(body.getRate())
                .customer(customer)
                .product(product)
                .build();

        List<GCPFile> gcpFiles = null;
        if (files != null && files.length != 0) {
            gcpFiles = fileService.uploadFiles(files);
            gcpFiles.forEach(gcpFile -> gcpFile.setReview(review));
        }
        review.setFiles(gcpFiles);

        ReviewResponse res = Mapper.toReviewResponse(reviewRepo.save(review));
        log.info("Review with ID = " + res.getReviewId() + " created successfully");
        orderItem.setReviewed(true);
        return res;
    }

    @Override
    public ProductReviewsRes getReviewsByProduct(Integer id, Integer rating, ReviewSortType sortType,
                                                 int pageNum, int pageSize) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));

        Page<Review> reviews = reviewRepo.findAllByProduct(product, PageRequest.of(pageNum, pageSize));
        long totalRateCount = reviews.getTotalElements();
        long fiveStarRateCount = reviews.filter(review -> review.getRate() == 5).stream().count();
        long fourStarRateCount = reviews.filter(review -> review.getRate() == 4).stream().count();
        long threeStarRateCount = reviews.filter(review -> review.getRate() == 3).stream().count();
        long twoStarRateCount = reviews.filter(review -> review.getRate() == 2).stream().count();
        long oneStarRateCount = reviews.filter(review -> review.getRate() == 1).stream().count();
        long totalRateValue = 0;
        for (Review review : reviews) totalRateValue += review.getRate();
        long avgRate = totalRateValue / totalRateCount;

        ProductReviewsRes resp = ProductReviewsRes.builder()
                .avgRate(avgRate)
                .totalRateCount(totalRateCount)
                .fiveStarRateCount(fiveStarRateCount)
                .fourStarRateCount(fourStarRateCount)
                .threeStarRateCount(threeStarRateCount)
                .twoStarRateCount(twoStarRateCount)
                .oneStarRateCount(oneStarRateCount)
                .build();

        if (sortType != null) {
            switch (sortType) {
                case RECENTLY -> resp.setReviews(getProductReviewsByRatingRecently(rating, product, pageNum, pageSize));
                case RATING_ASC -> resp.setReviews(getProductReviewsByRatingAsc(rating, product, pageNum, pageSize));
                case RATING_DESC -> resp.setReviews(getProductReviewsByRatingDesc(rating, product, pageNum, pageSize));
            }
        } else resp.setReviews(getProductReviewsByRatingRecently(rating, product, pageNum, pageSize));
        return resp;
    }

    private Page<ReviewResponse> getProductReviewsByRatingDesc(Integer rating, Product product, int pageNum, int pageSize) {
        if (rating != null)
            return reviewRepo
                    .findAllByProductAndRate(
                            product,
                            rating,
                            PageRequest.of(pageNum, pageSize, Sort.by("rate").descending()))
                    .map(Mapper::toReviewResponse);

        return reviewRepo
                .findAllByProduct(product, PageRequest.of(pageNum, pageSize, Sort.by("rate").descending()))
                .map(Mapper::toReviewResponse);
    }

    private Page<ReviewResponse> getProductReviewsByRatingAsc(Integer rating, Product product, int pageNum, int pageSize) {
        if (rating != null)
            return reviewRepo
                    .findAllByProductAndRate(
                            product,
                            rating,
                            PageRequest.of(pageNum, pageSize, Sort.by("rate").ascending()))
                    .map(Mapper::toReviewResponse);

        return reviewRepo
                .findAllByProduct(product, PageRequest.of(pageNum, pageSize, Sort.by("rate").ascending()))
                .map(Mapper::toReviewResponse);
    }

    private Page<ReviewResponse> getProductReviewsByRatingRecently(Integer rating, Product product,
                                                                   int pageNum, int pageSize) {
        if (rating != null)
            return reviewRepo
                    .findAllByProductAndRate(
                            product,
                            rating,
                            PageRequest.of(pageNum, pageSize, Sort.by("id").descending()))
                    .map(Mapper::toReviewResponse);

        return reviewRepo
                .findAllByProduct(product, PageRequest.of(pageNum, pageSize, Sort.by("id").descending()))
                .map(Mapper::toReviewResponse);
    }

    @Override
    @Transactional
    public ReviewResponse addShopFeedBack(Integer reviewId, String shopFeedBack) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        review.setShopFeedBack(shopFeedBack);
        return Mapper.toReviewResponse(review);
    }
}
