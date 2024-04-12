package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Integer> {
    boolean existsByShopId(Integer id);
}
