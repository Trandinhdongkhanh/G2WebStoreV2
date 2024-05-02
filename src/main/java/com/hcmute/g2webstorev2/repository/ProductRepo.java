package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.entity.ShopCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    boolean existsByNameAndShop(String name, Shop shop);

    @Query("select p from Product p order by rand(:seed)")
    Page<Product> findRandomProducts(Integer seed, Pageable pageable);
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

    @Query("select p from Product p " +
            "where p.category.path like :path% and " +
            "p.price between :startPrice and :endPrice " +
            "order by rand(:seed)")
    Page<Product> findAllByCategory(String path, Pageable pageable, Integer seed, Integer startPrice, Integer endPrice);

    @Query("select p from Product p where p.category.path like :path% order by p.productId desc")
    Page<Product> findNewestByCategory(String path, Pageable pageable);

    @Query("select p from Product p where p.category.path like :path% order by p.soldQuantity desc")
    Page<Product> findTopSellByCategory(String path, Pageable pageable);

    @Query("select p from Product p where p.category.path like :path% order by p.price asc")
    Page<Product> findAllByCategoryOrderByPriceAsc(String path, Pageable pageable);

    @Query("select p from Product p where p.category.path like :path% order by p.price desc")
    Page<Product> findAllByCategoryOrderByPriceDesc(String path, Pageable pageable);

    @Query("select p from Product p " +
            "where p.category.path like :path% " +
            "and p.price between :startPrice and :endPrice " +
            "order by p.price asc")
    Page<Product> findAllByCategoryOrderByPriceAsc(String path, Pageable pageable, Integer startPrice, Integer endPrice);

    @Query("select p from Product p " +
            "where p.category.path like :path% " +
            "and p.price between :startPrice and :endPrice " +
            "order by p.productId desc")
    Page<Product> findNewestByCategory(String path, Pageable pageable, Integer startPrice, Integer endPrice);

    @Query("select p from Product p " +
            "where p.category.path like :path% " +
            "and p.price between :startPrice and :endPrice " +
            "order by p.soldQuantity desc")
    Page<Product> findTopSellByCategory(String path, Pageable pageable, Integer startPrice, Integer endPrice);

    @Query("select p from Product p " +
            "where p.category.path like :path% " +
            "and p.price between :startPrice and :endPrice " +
            "order by p.price desc")
    Page<Product> findAllByCategoryOrderByPriceDesc(String path, Pageable pageable, Integer startPrice, Integer endPrice);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "order by rand(:seed)")
    Page<Product> findAllByName(String name, Pageable pageable, Integer seed);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice " +
            "order by rand(:seed)")
    Page<Product> findAllByName(String name, Pageable pageable, Integer startPrice, Integer endPrice, Integer seed);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "order by p.soldQuantity desc")
    Page<Product> findTopSellByName(String name, Pageable pageable);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice " +
            "order by p.soldQuantity desc")
    Page<Product> findTopSellByName(String name, Pageable pageable, Integer startPrice, Integer endPrice);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "order by p.productId desc")
    Page<Product> findNewestByName(String name, Pageable pageable);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice " +
            "order by p.productId desc")
    Page<Product> findNewestByName(String name, Pageable pageable, Integer startPrice, Integer endPrice);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "order by p.price desc")
    Page<Product> findAllByNameOrderByPriceDesc(String name, Pageable pageable);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice " +
            "order by p.price desc")
    Page<Product> findAllByNameOrderByPriceDesc(String name, Pageable pageable, Integer startPrice, Integer endPrice);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "order by p.price asc")
    Page<Product> findAllByNameOrderByPriceAsc(String name, Pageable pageable);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice " +
            "order by p.price asc")
    Page<Product> findAllByNameOrderByPriceAsc(String name, Pageable pageable, Integer startPrice, Integer endPrice);

    Page<Product> findAllByShopCategory(ShopCategory shopCategory, Pageable pageable);
}
