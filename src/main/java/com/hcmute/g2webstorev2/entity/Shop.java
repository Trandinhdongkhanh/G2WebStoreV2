package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Integer shopId;
    private String image;
    private String name;
    private String province;
    private String district;
    private Integer districtId;
    private String ward;
    private String street;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shop", fetch = FetchType.LAZY)
    private List<Product> products;
}
