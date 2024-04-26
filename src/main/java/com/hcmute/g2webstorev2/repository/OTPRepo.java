package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepo extends JpaRepository<OTP, Long> {
    Optional<OTP> findByVerificationCode(String verificationCode);
    @Modifying
    @Query("delete from OTP o where o.customer.customerId = :customerId")
    void deleteAllByCustomerId(Integer customerId);
    @Modifying
    @Query("delete from OTP o where o.seller.sellerId = :sellerId")
    void deleteAllBySellerId(Integer sellerId);
}
