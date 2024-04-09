package com.hcmute.g2webstorev2.entity;

import com.hcmute.g2webstorev2.enums.DiscountType;
import com.hcmute.g2webstorev2.enums.PromotionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "promotion_id")
    private String promotionId;
    private String name;
    private Date startDate;
    private Date endDate;
    private Integer maxUsePerCus;
    private Integer minSpend;
    private Integer specialPrice;
    private Integer stockQuantity;
    private Integer discountPrice;
    private Integer discountPercentage;
    @Enumerated(value = EnumType.STRING)
    private DiscountType discountType;
    @Enumerated(value = EnumType.STRING)
    private PromotionType promotionType;
    private boolean expired;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
}
