package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Integer> {
    boolean existsByShopId(Integer id);
    @Query("select s from Shop s where s.violationPoint = 5 and s.isAllowedToSell = true")
    Page<Shop> getReadyToBannedShops(Pageable pageable);
    @Query("select count(*) from Shop")
    Long countShops();
    @Query("select sum(s.balance) from Shop s")
    Long getTotalShopsBalance();
    @Query("select count(*) from Shop s where s.createdDate = :today")
    Long countTodayShops(LocalDate today);
}
