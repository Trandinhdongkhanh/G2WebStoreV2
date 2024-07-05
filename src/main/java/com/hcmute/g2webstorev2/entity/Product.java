package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product")
//@Document(indexName = "product")
public class Product {
//    @Id
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
//    @Field(type = FieldType.Keyword)
    private Integer productId;
//    @Field(type = FieldType.Text)
    private String name;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    @Field(type = FieldType.Nested)
    private List<GCPFile> images;
//    @Field(type = FieldType.Text)
    private String description;
//    @Field(type = FieldType.Integer)
    private Integer price;
//    @Field(type = FieldType.Integer)
    private Integer stockQuantity;
//    @Field(type = FieldType.Integer)
    private Integer soldQuantity;
//    @Field(type = FieldType.Float)
    private Float height;
//    @Field(type = FieldType.Float)
    private Float width;
//    @Field(type = FieldType.Float)
    private Float length;
//    @Field(type = FieldType.Float)
    private Float weight;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
//    @Field(type = FieldType.Object)
    private Shop shop;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
//    @Field(type = FieldType.Object)
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_cate_id")
//    @Field(type = FieldType.Object)
    private ShopCategory shopCategory;
//    @Field(type = FieldType.Boolean)
    private Boolean isAvailable;
//    @Field(type = FieldType.Boolean)
    private Boolean isBanned;
    @ManyToMany(mappedBy = "products", cascade = CascadeType.ALL)
//    @Field(type = FieldType.Nested)
    private List<Voucher> vouchers;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
//    @Field(type = FieldType.Nested)
    private Set<ShopItem> shopItems;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
//    @Field(type = FieldType.Nested)
    private List<Review> reviews;
}
