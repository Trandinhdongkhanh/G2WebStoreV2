package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.AddProductsToShopCateRequest;
import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.enums.SortType;
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
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @ModelAttribute ProductRequest body,
            @RequestParam("files") MultipartFile[] files) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.addProduct(body, files));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<String> updateProduct(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0") Integer id,
            @Valid @ModelAttribute ProductRequest body,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        productService.updateProduct(body, id, files);
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

            @RequestParam(value = "seed") @NotNull(message = "Seed cannot be null")
            Integer seed,

            @RequestParam(value = "sort", required = false) SortType sortType,
            @RequestParam(value = "startPrice", required = false) Integer startPrice,
            @RequestParam(value = "endPrice", required = false) Integer endPrice,
            @RequestParam(value = "districtId", required = false) Integer districtId) {
        return ResponseEntity.ok(productService.getProductsByCategory(
                id,
                pageNumber,
                pageSize,
                seed,
                sortType,
                startPrice,
                endPrice,
                districtId
        ));
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
            @NotBlank(message = "Product name cannot be blank") String name,
            @RequestParam(value = "sort", required = false) SortType sortType,
            @RequestParam(value = "startPrice", required = false) Integer startPrice,
            @RequestParam(value = "endPrice", required = false) Integer endPrice,
            @RequestParam(value = "districtId", required = false) Integer districtId) {
        return ResponseEntity.ok(productService.getProductsByName(
                name,
                pageNumber,
                pageSize,
                seed,
                sortType,
                startPrice,
                endPrice,
                districtId
        ));
    }

    @PutMapping("/shop-category/{id}")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<String> addProductsToShopCate(
            @PathVariable("id") Integer id,
            @Valid @RequestBody AddProductsToShopCateRequest body){
        productService.addProductsToShopCate(id, body);
        return ResponseEntity.ok("Add successfully");
    }
    @GetMapping("/shop-category/{id}")
    public ResponseEntity<Page<ProductResponse>> getProductsByShopCate(
            @PathVariable("id") Integer id,
            @RequestParam("page") int pageNumber,
            @RequestParam("size") int pageSize){
        return ResponseEntity.ok(productService.getProductsByShopCate(id, pageNumber, pageSize));
    }
}
