package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(value = "page")
            @Min(value = 0, message = "Page index must not be less than zero")
            @NotNull(message = "Page index must not be null")
            Integer pageNumber,

            @RequestParam(value = "size")
            @Min(value = 0, message = "Page size must not be less than 1")
            @NotNull(message = "Page size must not be null")
            Integer pageSize,

            @RequestParam(value = "seed")
            Integer seed) {
        //Seed is used to randomize products. Each time a seed change products get randomized
        return ResponseEntity.ok(productService.getAllProducts(pageNumber, pageSize, seed));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Page<ProductResponse>> getAllProductsByShop(
            @PathVariable("shopId")
            @NotNull(message = "Shop ID cannot be null")
            @Min(value = 1, message = "Shop ID must be greater than 0")
            Integer id,

            @RequestParam(value = "page")
            @Min(value = 0, message = "Page index must not be less than zero")
            @NotNull(message = "Page index must not be null")
            Integer pageNumber,

            @RequestParam(value = "size")
            @Min(value = 0, message = "Page size must not be less than 1")
            @NotNull(message = "Page size must not be null")
            Integer pageSize) {
        return ResponseEntity.ok(productService.getAllProductsByShop(id, pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0")
            Integer id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('CREATE_PRODUCT')")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest body) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.addProduct(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<String> updateProduct(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0") Integer id,
            @Valid @RequestBody ProductRequest body) {
        productService.updateProduct(body, id);
        return ResponseEntity.ok("Product with ID = " + id + " updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('DELETE_PRODUCT')")
    public ResponseEntity<String> delProduct(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0") Integer id) {
        productService.delProduct(id);
        return ResponseEntity.ok("Product with ID = " + id + " updated successfully");
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @PathVariable("categoryId")
            @NotNull(message = "Category ID cannot be null")
            @Min(value = 1, message = "Category ID must be greater than 0")
            Integer id,

            @RequestParam(value = "page")
            @Min(value = 0, message = "Page index must not be less than zero")
            @NotNull(message = "Page index must not be null")
            Integer pageNumber,

            @RequestParam(value = "size")
            @Min(value = 0, message = "Page size must not be less than 1")
            @NotNull(message = "Page size must not be null")
            Integer pageSize,

            @RequestParam(value = "seed")
            @NotNull(message = "Seed cannot be null")
            Integer seed) {
        return ResponseEntity.ok(productService.getProductsByCategory(id, pageNumber, pageSize, seed));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> getProductsByName(
            @RequestParam(value = "page")
            @Min(value = 0, message = "Page index must not be less than zero")
            @NotNull(message = "Page index must not be null")
            Integer pageNumber,

            @RequestParam(value = "size")
            @Min(value = 0, message = "Page size must not be less than 1")
            @NotNull(message = "Page size must not be null")
            Integer pageSize,

            @RequestParam(value = "seed")
            @NotNull(message = "Seed cannot be null")
            Integer seed,

            @RequestParam("name")
            @NotBlank(message = "Product name cannot be blank")
            String name) {
        return ResponseEntity.ok(productService.getProductsByName(pageNumber, pageSize, name, seed));
    }
}
