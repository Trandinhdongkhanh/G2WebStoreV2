package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Integer> {
    List<Review> findAllByCustomer(Customer customer);
    boolean existsByCustomerAndProduct(Customer customer, Product product);
    Page<Review> findAllByProduct(Product product, Pageable pageable);
    Page<Review> findAllByProductAndRate(Product product, Integer rate, Pageable pageable);
    @Query("select count(*) from Review r where r.rate = :rate")
    long countReviewsByRate(Integer rate);
    @Query("select count(*) from Review r")
    long countReviews();
    @Query("SELECT SUM(r.rate) FROM Review r")
    long sumOfRates();
}
