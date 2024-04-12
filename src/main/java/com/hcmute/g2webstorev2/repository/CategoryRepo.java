package com.hcmute.g2webstorev2.repository;

import com.hcmute.g2webstorev2.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);

//    @Query(value = "with recursive cte(category_id, name, parent_id) as ( " +
//            "select category_id, name, parent_id " +
//            "from category " +
//            "where parent_id = :parentId " +
//            "union all " +
//            "select c.category_id, c.name, c.parent_id " +
//            "from category c " +
//            "inner join cte " +
//            "on c.parent_id = cte.category_id) " +
//            "select * from cte", nativeQuery = true)
//    List<Category> findAllByParentCategory(Integer parentId);

    @Query("select c from Category c " +
            "where c.parentCategory.categoryId is null")
    List<Category> findAllCategories();
}
