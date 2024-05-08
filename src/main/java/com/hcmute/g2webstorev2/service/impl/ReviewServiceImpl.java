package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ReviewRequest;
import com.hcmute.g2webstorev2.dto.response.ReviewResponse;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.GCPFile;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Review;
import com.hcmute.g2webstorev2.enums.ReviewSortType;
import com.hcmute.g2webstorev2.exception.ProductReviewedException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
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

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest body, MultipartFile[] files) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product product = productRepo.findById(body.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + body.getProductId() + " not found"));

        if (reviewRepo.existsByCustomerAndProduct(customer, product))
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
        return res;
    }

    @Override
    public Page<ReviewResponse> getReviewsByProduct(Integer id, Integer rating, ReviewSortType sortType,
                                                    int pageNum, int pageSize) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));

        if (sortType != null) {
            switch (sortType) {
                case RECENTLY -> {
                   return getProductReviewsByRatingRecently(rating, product, pageNum, pageSize);
                }
                case RATING_ASC -> {
                    return getProductReviewsByRatingAsc(rating, product, pageNum, pageSize);
                }
                case RATING_DESC -> {
                    return getProductReviewsByRatingDesc(rating, product, pageNum, pageSize);
                }
            }
        }
        return getProductReviewsByRatingRecently(rating, product, pageNum, pageSize);
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
