package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer> {
    Optional<Seller> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Seller> findAllByShop(Shop shop);
    boolean existsByPhoneNo(String phoneNo);

    @Query("select s from Seller s where s.shop = :shop and s.isMainAcc = true")
    Optional<Seller> findByShopAndIsMainAcc(Shop shop);
}
