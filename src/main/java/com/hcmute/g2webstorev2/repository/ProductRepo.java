package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    boolean existsByNameAndShop(String name, Shop shop);
    @Query("select p from Product p order by rand(:seed)")
    Page<Product> findRandomProducts(Integer seed, Pageable pageable);
    @Query("select p from Product p where p.name like :name% order by rand(:seed)")
    Page<Product> findRandomProductsByName(Integer seed, Pageable pageable, String name);
    @Query("select p from Product p where p.price between :startPrice and :endPrice order by rand(:seed)")
    Page<Product> findRandomProductsByPriceBetween(Integer seed, Pageable pageable, Integer startPrice, Integer endPrice);
    Page<Product> findAllByShop(Shop shop, Pageable pageable);
}
