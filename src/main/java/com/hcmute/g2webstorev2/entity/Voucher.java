package com.hcmute.g2webstorev2.entity;

import com.hcmute.g2webstorev2.enums.DiscountType;
import com.hcmute.g2webstorev2.enums.VoucherType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "voucher_id")
    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(value = EnumType.STRING)
    private DiscountType discountType;
    @Enumerated(value = EnumType.STRING)
    private VoucherType voucherType;
    private Integer minSpend;
    private Integer reducePrice;
    private Integer reducePercent;
    private Integer quantity;
    private Integer maxUsePerCus;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "voucher_product",
            joinColumns = @JoinColumn(name = "voucher_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private Shop shop;
}