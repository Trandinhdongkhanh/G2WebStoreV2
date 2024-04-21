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
//    @Query(nativeQuery = true, value = """
//            WITH recursive cte AS (
//                SELECT category_id
//                FROM category
//                WHERE category_id = :categoryId
//                UNION ALL
//                SELECT c.category_id
//                FROM category c
//                         JOIN cte rc ON c.parent_id = rc.category_id
//            )
//            SELECT p
//            FROM product p
//                     JOIN cte rc ON p.category_id = rc.category_id
//                     order by rand(:seed)""")
//    Page<Product> findAllByCategory(Integer categoryId, Pageable pageable, Integer seed);

    @Query("select p from Product p where p.category.path like :path% order by rand(:seed)")
    Page<Product> findAllByCategory(String path, Pageable pageable, Integer seed);
}
