package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.entity.ShopCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    boolean existsByNameAndShop(String name, Shop shop);
    @Query("select p from Product p " +
            "where p.price between :startPrice and :endPrice " +
            "and p.shop.isAllowedToSell = true " +
            "order by rand(:seed)")
    Page<Product> findAllByPriceBetween(Integer startPrice, Integer endPrice, Integer seed, Pageable pageable);
    @Query("select p from Product p " +
            "where p.price between :startPrice and :endPrice " +
            "and p.shop.isAllowedToSell = true")
    Page<Product> findAllByPriceBetween(Integer startPrice, Integer endPrice, Pageable pageable);
    Page<Product> findAllByShop(Shop shop, Pageable pageable);
    @Query("select p from Product p order by rand(:seed)")
    Page<Product> findAll(Integer seed, Pageable pageable);
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

    @Query("select p from Product p where p.category.path like :path% " +
            "and p.price between :startPrice and :endPrice")
    Page<Product> findAllByCategoryAndPriceBetween(String path, Integer startPrice, Integer endPrice, Pageable pageable);
    @Query("select p from Product p where p.category.path like :path% " +
            "and p.price between :startPrice and :endPrice " +
            "order by rand(:seed)")
    Page<Product> findAllByCategoryAndPriceBetween(String path, Integer startPrice, Integer endPrice, Pageable pageable, Integer seed);

    @Query("select p from Product p where p.category.path like :path% order by rand(:seed)")
    Page<Product> findAllByCategory(String path, Pageable pageable, Integer seed);
    @Query("select p from Product p where p.category.path like :path%")
    Page<Product> findAllByCategory(String path, Pageable pageable);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "order by rand(:seed)")
    Page<Product> findAllByName(String name, Pageable pageable, Integer seed);

    @Query("select p from Product p " +
            "where p.name like :name%")
    Page<Product> findAllByName(String name, Pageable pageable);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice")
    Page<Product> findAllByNameAndPriceBetween(String name, Integer startPrice, Integer endPrice, Pageable pageable);
    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice " +
            "order by rand(:seed)")
    Page<Product> findAllByNameAndPriceBetween(String name, Integer startPrice, Integer endPrice, Pageable pageable, Integer seed);

    Page<Product> findAllByShopCategory(ShopCategory shopCategory, Pageable pageable);
    @Query("select count(*) from Product p " +
            "where p.shop.shopId = :shopId")
    long countOnSaleProduct(Integer shopId);
    @Query("select count(*) from Product p " +
            "where p.shop.shopId = :shopId and p.stockQuantity = 0")
    long countOutOfStockProduct(Integer shopId);
}
