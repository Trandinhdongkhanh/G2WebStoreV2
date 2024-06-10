package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;


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
    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private GCPFile image;
    private String name;
    private String province;
    private String district;
    private Integer districtId;
    private String ward;
    private String street;
    private Long balance;
    private Integer violationPoint;
    private Boolean isAllowedToSell;
}
