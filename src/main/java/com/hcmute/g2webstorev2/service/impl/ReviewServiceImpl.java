package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.ReviewRequest;
import com.hcmute.g2webstorev2.dto.response.ReviewResponse;
import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.GCPFile;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Review;
import com.hcmute.g2webstorev2.exception.ProductReviewedException;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.repository.ReviewRepo;
import com.hcmute.g2webstorev2.service.FileService;
import com.hcmute.g2webstorev2.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<ReviewResponse> getReviewByProduct(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID = " + id + " not found"));
        return reviewRepo.findAllByProduct(product)
                .stream().map(Mapper::toReviewResponse)
                .collect(Collectors.toList());
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
