package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepo extends JpaRepository<Review, Integer> {
    Page<Review> findAllByProduct(Product product, Pageable pageable);
    Page<Review> findAllByProductAndRate(Product product, Integer rate, Pageable pageable);
    @Query("select count(*) from Review r where r.rate = :rate and r.product.productId = :productId")
    Long countReviewsByRateAndProduct(Integer rate, Integer productId);
    @Query("select count(*) from Review r where r.product.productId = :productId")
    Long countReviewsByProduct(Integer productId);
    @Query("SELECT SUM(r.rate) FROM Review r where r.product.productId = :productId")
    Long sumOfRatesByProduct(Integer productId);
    @Query("select r from Review r " +
            "where r.shopFeedBack is null and " +
            "r.product.shop.shopId = :shopId")
    Page<Review> findAllUnFeedbackReviewByShop(Integer shopId, Pageable pageable);
    @Query("select r from Review r " +
            "where r.shopFeedBack is null and " +
            "r.product.shop.shopId = :shopId and " +
            "r.rate = :rate")
    Page<Review> findAllUnFeedbackReviewByShopAndRate(Integer shopId, Integer rate, Pageable pageable);
}
