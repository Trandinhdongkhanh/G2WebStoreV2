package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(value = "page", required = false) int pageNumber,
            @RequestParam(value = "size", required = false) int pageSize) {
        return ResponseEntity.ok(productService.getAllProducts(pageNumber, pageSize));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductResponse>> getAllProductsByShop(
            @PathVariable("shopId")
            @NotNull(message = "Shop ID cannot be null")
            @Min(value = 1, message = "Shop ID must be greater than 0") Integer id) {
        return ResponseEntity.ok(productService.getAllProductsByShop(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0") Integer id) {
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
    public ResponseEntity<?> updateProduct(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0") Integer id,
            @Valid @RequestBody ProductRequest body) {
        productService.updateProduct(body, id);
        return ResponseEntity.ok("Product with ID = " + id + " updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('DELETE_PRODUCT')")
    public ResponseEntity<?> delProduct(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0") Integer id) {
        productService.delProduct(id);
        return ResponseEntity.ok("Product with ID = " + id + " updated successfully");
    }
}
