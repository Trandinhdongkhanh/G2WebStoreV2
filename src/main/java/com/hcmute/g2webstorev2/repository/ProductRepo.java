package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    boolean existsByNameAndShop(String name, Shop shop);

    @Query("select p from Product p where p.shop.shopId = :shopId")
    List<Product> findAllByShop(Integer shopId);

}
