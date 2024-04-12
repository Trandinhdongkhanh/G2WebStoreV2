package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.ShopCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopCateRepo extends JpaRepository<ShopCategory, Integer> {
    @Query("select sc from ShopCategory sc where sc.parentCategory is null")
    List<ShopCategory> findAllShopCategories();

    boolean existsByNameAndShop_ShopId(String name, Integer id);

    @Query("select sc from ShopCategory sc where sc.parentCategory is null and sc.shop.shopId = :id")
    List<ShopCategory> findAllShopCategoriesByShop(Integer id);
}
