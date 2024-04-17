package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    boolean existsByNameAndShop(String name, Shop shop);
    List<Product> findAllByShop(Shop shop);
    @Query("select p from Product p where p.shop.shopId = :shopId order by rand() limit 1")
    Product findRandomProductByShopId(Integer shopId);
}
