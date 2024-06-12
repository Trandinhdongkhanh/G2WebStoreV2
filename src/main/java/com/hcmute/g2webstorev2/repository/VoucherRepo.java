package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepo extends JpaRepository<Voucher, String> {
    boolean existsByName(String name);
    @Query("select v from Voucher v join v.products p where p.productId = :productId")
    List<Voucher> findAllByProductId(Integer productId);
    List<Voucher> findAllByShop(Shop shop);
    List<Voucher> findAllByShopAndNameStartingWith(Shop shop, String name);
    List<Voucher> findAllByShopAndVoucherId(Shop shop, String id);
    Page<Voucher> findAllByShopAndIsPausedIsTrue(Shop shop, Pageable pageable);
}
