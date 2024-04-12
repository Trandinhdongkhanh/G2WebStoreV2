package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.CategoryRequest;
import com.hcmute.g2webstorev2.dto.response.CategoryResponse;
import com.hcmute.g2webstorev2.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_CATEGORY')")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.addCategory(body));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('UPDATE_CATEGORY')")
    public ResponseEntity<String> updateCategory(
            @Valid @RequestBody CategoryRequest body,
            @PathVariable("id")
            @NotNull(message = "Category ID cannot be null")
            @Min(value = 1, message = "Category ID must be greater than 0") Integer id) {
        categoryService.updateCategory(body, id);
        return ResponseEntity.ok("Category with ID = " + id + " updated successfully");
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable("id")
            @NotNull(message = "Category ID cannot be null")
            @Min(value = 1, message = "Category ID must be greater than 0") Integer id
    ) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE_CATEGORY')")
    public ResponseEntity<String> delCategory(
            @PathVariable("id")
            @NotNull(message = "Category ID cannot be null")
            @Min(value = 1, message = "Category ID must be greater than 0") Integer id
    ) {
        categoryService.delCategory(id);
        return ResponseEntity.ok("Category with ID = " + id + " deleted successfully");
    }
}
