package com.hcmute.g2webstorev2.es.index;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "product_index")
public class ProductIndex {
    @Id
    @Field(type = FieldType.Keyword, name = "product_id")
    private Integer productId;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Integer)
    private Integer price;
    @Field(type = FieldType.Integer, name = "stock_quantity")
    private Integer stockQuantity;
    @Field(type = FieldType.Integer, name = "sold_quantity")
    private Integer soldQuantity;
    @Field(type = FieldType.Integer, name = "shop_id")
    private Integer shopId;
    @Field(type = FieldType.Integer, name = "category_id")
    private Integer categoryId;
    @Field(type = FieldType.Integer, name = "shop_category_id")
    private Integer shopCategoryId;
    @Field(type = FieldType.Boolean, name = "is_available")
    private Boolean isAvailable;
    @Field(type = FieldType.Boolean, name = "is_banned")
    private Boolean isBanned;
}
