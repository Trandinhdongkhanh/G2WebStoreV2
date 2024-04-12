package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.request.CategoryRequest;
import com.hcmute.g2webstorev2.dto.response.CategoryResponse;
import com.hcmute.g2webstorev2.entity.Category;
import com.hcmute.g2webstorev2.exception.ResourceNotFoundException;
import com.hcmute.g2webstorev2.exception.ResourceNotUniqueException;
import com.hcmute.g2webstorev2.mapper.Mapper;
import com.hcmute.g2webstorev2.repository.CategoryRepo;
import com.hcmute.g2webstorev2.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepo.findAllByParentCategory()
                .stream()
                .map(Mapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCategory(CategoryRequest body, Integer id) {
        if (categoryRepo.existsByName(body.getName()))
            throw new ResourceNotUniqueException("Duplicate category name");
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + id + " not found"));
        Category parentCategory = null;
        if (body.getParentId() != null) {
            parentCategory = categoryRepo.findById(body.getParentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Parent category with ID = " + body.getParentId() + " not found"));
        }
        category.setParentCategory(parentCategory);
        category.setName(body.getName());
    }

    @Override
    @Transactional
    public void delCategory(Integer id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + id + " not found"));

        if (category.getParentCategory() != null) {
            category.getParentCategory().getChildCategories().remove(category);
            category.setParentCategory(null);
        }

        categoryRepo.delete(category);

        log.info("Category with ID = " + id + " deleted successfully");
    }

    @Override
    @Transactional
    public CategoryResponse addCategory(CategoryRequest body) {
        if (categoryRepo.existsByName(body.getName()))
            throw new ResourceNotUniqueException("Duplicate category name");
        Category parentCategory = null;
        if (body.getParentId() != null) {
            parentCategory = categoryRepo.findById(body.getParentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Parent category with ID = " + body.getParentId() + " not found"));
        }

        CategoryResponse res = Mapper.toCategoryResponse(categoryRepo.save(
                Category.builder()
                        .name(body.getName())
                        .parentCategory(parentCategory)
                        .build()));
        log.info("Category created successfully");
        return res;
    }

    @Override
    public CategoryResponse getCategory(Integer id) {
        return Mapper.toCategoryResponse(categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID = " + id + " not found")));
    }
}
