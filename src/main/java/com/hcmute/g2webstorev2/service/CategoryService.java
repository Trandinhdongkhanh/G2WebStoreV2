package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.CategoryRequest;
import com.hcmute.g2webstorev2.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategory(Integer id);
    void updateCategory(CategoryRequest body, Integer id);
    void delCategory(Integer id);
    CategoryResponse addCategory(CategoryRequest body);
}
