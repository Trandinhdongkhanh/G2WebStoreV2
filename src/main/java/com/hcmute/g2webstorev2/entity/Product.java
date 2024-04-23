package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    private String name;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<GCPFile> images;
    private String description;
    private Integer price;
    private Integer specialPrice;
    private Integer stockQuantity;
    private Integer soldQuantity;
    private Float height;
    private Float width;
    private Float length;
    private Float weight;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_cate_id")
    private ShopCategory shopCategory;
    @ManyToMany(mappedBy = "products")
    private List<Voucher> vouchers;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private List<CartItem> items;
}
