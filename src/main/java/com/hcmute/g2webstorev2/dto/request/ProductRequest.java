package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    @NotBlank(message = "Product images cannot be blank")
    private String images;
    @NotBlank(message = "Product description cannot be blank")
    private String description;
    @NotNull(message = "Product price cannot be null")
    @Min(value = 0, message = "Product price must be equals or greater than 0")
    private Integer price;
    @JsonProperty("special_price")
    @Min(value = 0, message = "Product special price must be equals or greater than 0")
    private Integer specialPrice;
    @JsonProperty("stock_quantity")
    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 0, message = "Product quantity must be equals or greater than 0")
    private Integer stockQuantity;
    @NotNull(message = "Height cannot be null")
    @Min(value = 0, message = "Height must be equals or greater than 0")
    private Float height;
    @NotNull(message = "Width cannot be null")
    @Min(value = 0, message = "Width must be equals or greater than 0")
    private Float width;
    @NotNull(message = "Length cannot be null")
    @Min(value = 0, message = "Length must be equals or greater than 0")
    private Float length;
    @NotNull(message = "Weight cannot be null")
    @Min(value = 0, message = "Weight must be equals or greater than 0")
    private Float weight;
    @JsonProperty("category_id")
    @NotNull(message = "Category ID cannot be null")
    @Min(value = 1, message = "Category ID must be equals or greater than 1")
    private Integer categoryId;

}
