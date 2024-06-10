package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Integer> {
    boolean existsByShopId(Integer id);
    @Query("select s from Shop s where s.violationPoint = 5 and s.isAllowedToSell = true")
    Page<Shop> getReadyToBannedShops(Pageable pageable);
}
