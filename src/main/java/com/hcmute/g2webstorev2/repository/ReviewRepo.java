package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Customer;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Integer> {
    List<Review> findAllByCustomer(Customer customer);
    boolean existsByCustomerAndProduct(Customer customer, Product product);
    Page<Review> findAllByProduct(Product product, Pageable pageable);
    Page<Review> findAllByProductAndRate(Product product, Integer rate, Pageable pageable);
}
