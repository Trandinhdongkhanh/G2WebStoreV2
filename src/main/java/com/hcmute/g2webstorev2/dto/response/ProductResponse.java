package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.entity.Shop;
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
    private ShopResponse shop;
    private CategoryResponse category;
}
