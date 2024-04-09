package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.request.CategoryRequest;
import com.hcmute.g2webstorev2.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();
    void updateCategory(CategoryRequest body, Integer id);
    void delCategory(Integer id);
    CategoryResponse addCategory(CategoryRequest body);
    CategoryResponse getCategory(Integer id);
}
