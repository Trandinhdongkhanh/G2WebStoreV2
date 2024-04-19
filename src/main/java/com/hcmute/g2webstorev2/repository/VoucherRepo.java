package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepo extends JpaRepository<Voucher, String> {
    boolean existsByName(String name);
    @Query("select v from Voucher v join v.products p where p.productId = :productId")
    List<Voucher> findAllByProductId(Integer productId);
}
