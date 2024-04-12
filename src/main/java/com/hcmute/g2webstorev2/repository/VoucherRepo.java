package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepo extends JpaRepository<Voucher, String> {
    boolean existsByName(String name);
}
