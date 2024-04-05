package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer> {
    Optional<Seller> findByEmail(String email);
    boolean existsByEmail(String email);
}
