package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNo(String phoneNo);
    @Query("select count(*) from Customer")
    Long countCustomers();
    @Query("select count(*) from Customer c where c.createdDate = :today")
    Long countTodayCustomers(LocalDate today);
}
