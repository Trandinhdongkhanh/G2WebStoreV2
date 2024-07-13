package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.request.AddProductsToExportExcelReq;
import com.hcmute.g2webstorev2.dto.request.AddProductsToShopCateRequest;
import com.hcmute.g2webstorev2.dto.request.ProductRequest;
import com.hcmute.g2webstorev2.dto.response.ProductResponse;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.enums.ShopProductsSortType;
import com.hcmute.g2webstorev2.enums.SortType;
import com.hcmute.g2webstorev2.es.index.ProductIndex;
import com.hcmute.g2webstorev2.service.ExcelService;
import com.hcmute.g2webstorev2.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ExcelService excelService;

    @PutMapping("/update-batch")
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_FULL_ACCESS'," +
            "'SELLER_PRODUCT_ACCESS') or hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<String> updateProductsFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        List<Product> products = excelService.readProductsData(file);
        productService.updateProducts(products);
        return ResponseEntity.ok("Update products successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable("id")
            @NotNull(message = "Product ID cannot be null")
            @Min(value = 1, message = "Product ID must be greater than 0")
            Integer id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping("/export/excel")
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_READ_ONLY'," +
            "'SELLER_FULL_ACCESS'," +
            "'SELLER_PRODUCT_ACCESS') or hasAuthority('READ_PRODUCT')")
    public void exportToExcel(HttpServletResponse res, @RequestBody @Valid AddProductsToExportExcelReq body) throws IOException {
        excelService.exportToExcel(res, body);
    }

    @GetMapping
    public ResponseEntity<Page<ProductIndex>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0")
            @Min(value = 0, message = "Page index must not be less than zero")
            @NotNull(message = "Page index must not be null")
            Integer pageNumber,

            @RequestParam(value = "size", defaultValue = "12")
            @Min(value = 0, message = "Page size must not be less than 1")
            @NotNull(message = "Page size must not be null")
            Integer pageSize,

            @RequestParam(value = "seed") Integer seed,
            @RequestParam(value = "sort", required = false) SortType sortType,
            @RequestParam(value = "startPrice", required = false) Integer startPrice,
            @RequestParam(value = "endPrice", required = false) Integer endPrice,
            @RequestParam(value = "districtId", required = false) Integer districtId) {
        //Seed is used to randomize products. Each time a seed change products get randomized
        return ResponseEntity.ok(productService.getAllProducts(
                pageNumber, pageSize, seed, sortType, startPrice, endPrice, districtId));
    }

    @GetMapping("/top-five/shop/{shopId}")
    public ResponseEntity<List<ProductResponse>> getTopFivePopularProducts(
            @PathVariable("shopId")
            @NotNull(message = "Shop ID must not null")
            Integer shopId) {
        return ResponseEntity.ok(productService.getTopFivePopularProductByShop(shopId));
    }

    @GetMapping("/shop/me")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'SELLER_PRODUCT_ACCESS', 'SELLER_READ_ONLY') or " +
            "hasAuthority('READ_PRODUCT')")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @RequestParam(value = "page")
            @Min(value = 0, message = "Page index must not be less than zero")
            @NotNull(message = "Page index must not be null")
            Integer pageNumber,

            @RequestParam(value = "size")
            @Min(value = 0, message = "Page size must not be less than 1")
            @NotNull(message = "Page size must not be null")
            Integer pageSize,
            @RequestParam(value = "shopProductSortType", required = false) ShopProductsSortType sortType) {
        return ResponseEntity.ok(productService.sellerGetAllProductsByShop(pageNumber, pageSize, sortType));
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Page<ProductResponse>> customerGetAllProductsByShop(
            @PathVariable("shopId") Integer shopId,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "size") int size,
            @RequestParam(defaultValue = "DEFAULT", name = "sort") SortType sortType,
            @RequestParam(value = "startPrice", required = false) Integer startPrice,
            @RequestParam(value = "endPrice", required = false) Integer endPrice,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "shopCateId", required = false) Integer shopCateId
    ) {
        return ResponseEntity.ok(productService.customerGetAllProductsByShop(
                shopId, sortType, page, size, startPrice, endPrice, name, shopCateId
        ));
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<ProductResponse> enableProduct(
            @PathVariable("id") Integer productId,
            @RequestParam(defaultValue = "true", name = "isAvailable") boolean isAvailable) {
        return ResponseEntity.ok(productService.enableProduct(isAvailable, productId));
    }

    @PutMapping("/{id}/banned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> banProduct(
            @PathVariable("id") Integer productId,
            @RequestParam(defaultValue = "true", name = "isBanned") boolean isBanned
    ) {
        return ResponseEntity.ok(productService.bannedProduct(isBanned, productId));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('CREATE_PRODUCT')")
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @ModelAttribute ProductRequest body,
            @RequestParam("files") MultipartFile[] files) throws IOException {
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

    @PutMapping("/{id}/del-image")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<ProductResponse> delImage(
            @PathVariable("id") Integer productId,
            @RequestParam("fileId") Long fileId) {
        return ResponseEntity.ok(productService.delImage(productId, fileId));
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
    public ResponseEntity<Page<ProductIndex>> getProductsByCategory(
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
                id, pageNumber, pageSize, seed, sortType, startPrice, endPrice, districtId
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductIndex>> getProductsByName(
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
            @RequestParam(value = "districtId", required = false) Integer districtId) throws IOException {
        return ResponseEntity.ok(productService.getProductsByName(
                name, pageNumber, pageSize, seed, sortType, startPrice, endPrice, districtId
        ));
    }

    @PutMapping("/shop-category/{id}")
    @PreAuthorize("hasAnyRole('SELLER_PRODUCT_ACCESS', 'SELLER_FULL_ACCESS') or hasAuthority('UPDATE_PRODUCT')")
    public ResponseEntity<String> addProductsToShopCate(
            @PathVariable("id") Integer id,
            @Valid @RequestBody AddProductsToShopCateRequest body) {
        productService.addProductsToShopCate(id, body);
        return ResponseEntity.ok("Add successfully");
    }

    @GetMapping("/shop-category/{id}")
    @PreAuthorize("hasAnyRole('SELLER_FULL_ACCESS', 'SELLER_PRODUCT_ACCESS', 'SELLER_READ_ONLY') " +
            "or hasAuthority('READ_PRODUCT')")
    public ResponseEntity<Page<ProductResponse>> getProductsByShopCate(
            @PathVariable("id") Integer id,
            @RequestParam("page") int pageNumber,
            @RequestParam("size") int pageSize) {
        return ResponseEntity.ok(productService.getProductsByShopCate(id, pageNumber, pageSize));
    }
}