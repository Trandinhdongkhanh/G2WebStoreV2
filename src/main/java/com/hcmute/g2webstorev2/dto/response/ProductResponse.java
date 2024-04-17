package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    @JsonProperty("product_id")
    private Integer productId;
    private String name;
    private String images;
    private String description;
    private Integer price;
    @JsonProperty("special_price")
    private Integer specialPrice;
    @JsonProperty("stock_quantity")
    private Integer stockQuantity;
    @JsonProperty("sold_quantity")
    private Integer soldQuantity;
    private Float height;
    private Float width;
    private Float length;
    private Float weight;
    private ShopResponse shop;
    private CategoryResponse category;
}
