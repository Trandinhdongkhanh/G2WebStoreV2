package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.entity.Shop;
import com.hcmute.g2webstorev2.entity.ShopCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    boolean existsByNameAndShop(String name, Shop shop);
    @Query("select p from Product p " +
            "where p.price between :startPrice and :endPrice " +
            "and p.isAvailable = true " +
            "order by rand(:seed)")
    Page<Product> findAllByPriceBetween(Integer startPrice, Integer endPrice, Integer seed, Pageable pageable);
    @Query("select p from Product p " +
            "where p.price between :startPrice and :endPrice " +
            "and p.isAvailable = true")
    Page<Product> findAllByPriceBetween(Integer startPrice, Integer endPrice, Pageable pageable);
    @Query("select p from Product p " +
            "where p.shop = :shop")
    Page<Product> sellerFindAllByShop(Shop shop, Pageable pageable);
    @Query("select p from Product p " +
            "where p.shop = :shop")
    Page<Product> sellerFindAllByShopProductIncomeDesc(Shop shop, Pageable pageable);
    @Query("select p from Product p " +
            "where p.shop = :shop")
    List<Product> sellerGetAllProductsByShop(Shop shop);
    @Query("select p from Product p " +
            "where p.shop = :shop and p.isAvailable = true")
    Page<Product> customerFindAllByShop(Shop shop, Pageable pageable);
    @Query("select p from Product p " +
            "where p.shop = :shop and p.isAvailable = true")
    List<Product> customerFindAllByShop(Shop shop);
    @Query("select p from Product p " +
            "where p.shop = :shop and p.isAvailable = true " +
            "and p.name like :name%")
    List<Product> customerFindAllByShopAndName(Shop shop, String name);

    @Query("select p from Product p where p.isAvailable = true order by rand(:seed)")
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
            "and p.price between :startPrice and :endPrice " +
            "and p.isAvailable = true")
    Page<Product> findAllByCategoryAndPriceBetween(String path, Integer startPrice, Integer endPrice, Pageable pageable);
    @Query("select p from Product p where p.category.path like :path% " +
            "and p.price between :startPrice and :endPrice " +
            "and p.isAvailable = true " +
            "order by rand(:seed)")
    Page<Product> findAllByCategoryAndPriceBetween(String path, Integer startPrice, Integer endPrice, Pageable pageable, Integer seed);

    @Query("select p from Product p where p.category.path like :path% and p.isAvailable = true order by rand(:seed)")
    Page<Product> findAllByCategory(String path, Pageable pageable, Integer seed);
    @Query("select p from Product p where p.category.path like :path% and p.isAvailable = true")
    Page<Product> findAllByCategory(String path, Pageable pageable);
    @Query("select p from Product p where p.category.path like :path% and p.isAvailable = true")
    List<Product> findAllByCategory(String path);
    Page<Product> findAllByShopCategory(ShopCategory shopCategory, Pageable pageable);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.isAvailable = true " +
            "order by rand(:seed)")
    Page<Product> findAllByName(String name, Pageable pageable, Integer seed);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.isAvailable = true")
    Page<Product> findAllByName(String name, Pageable pageable);

    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice " +
            "and p.isAvailable = true")
    Page<Product> findAllByNameAndPriceBetween(String name, Integer startPrice, Integer endPrice, Pageable pageable);
    @Query("select p from Product p " +
            "where p.name like :name% " +
            "and p.price between :startPrice and :endPrice " +
            "and p.isAvailable = true " +
            "order by rand(:seed)")
    Page<Product> findAllByNameAndPriceBetween(String name, Integer startPrice, Integer endPrice, Pageable pageable, Integer seed);

    @Query("select count(*) from Product p " +
            "where p.shop.shopId = :shopId")
    long countOnSaleProduct(Integer shopId);
    List<Product> findAllByShop(Shop shop);
    @Query("select count(*) from Product p " +
            "where p.shop.shopId = :shopId and p.stockQuantity = 0")
    long countOutOfStockProduct(Integer shopId);
}
