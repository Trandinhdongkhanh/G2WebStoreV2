package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

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
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private Integer price;
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
    private Boolean isAvailable;
    private Boolean isBanned;
    @ManyToMany(mappedBy = "products", cascade = CascadeType.ALL)
    private List<Voucher> vouchers;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private Set<ShopItem> shopItems;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private List<Review> reviews;
}
