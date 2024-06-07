package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.hcmute.g2webstorev2.enums.VoucherType.*;
import static com.hcmute.g2webstorev2.enums.DiscountType.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_item_v2")
public class CartItemV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_v2_id")
    private Long cartItemId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "cartItemV2")
    private List<ShopItem> shopItems;
    @ManyToMany
    @JoinTable(
            name = "cart_item_voucher",
            joinColumns = @JoinColumn(name = "cart_item_v2_id"),
            inverseJoinColumns = @JoinColumn(name = "voucher_id")
    )
    private Set<Voucher> vouchers;

    @Transient
    private Long shopReduce;

    public Long getShopReduce() {
        if (vouchers == null || vouchers.isEmpty()) return 0L;
        long shopReduce = 0L;
        for (Voucher voucher : getVouchers()) {
            if (Objects.equals(SHOP_VOUCHER, voucher.getVoucherType())) {
                if (Objects.equals(PRICE, voucher.getDiscountType())) {
                    shopReduce += voucher.getReducePrice();
                    continue;
                }
                shopReduce += shopReduce * (voucher.getReducePercent() / 100);
            }
        }
        return shopReduce;
    }

    @Transient
    private Long feeShipReduce;

    public Long getFeeShipReduce() {
        if (vouchers == null || vouchers.isEmpty()) return 0L;
        long feeShipReduce = 0L;
        for (Voucher voucher : getVouchers()) {
            if (Objects.equals(FREE_SHIP_VOUCHER, voucher.getVoucherType())) {
                if (Objects.equals(PRICE, voucher.getDiscountType())) {
                    feeShipReduce += voucher.getReducePrice();
                    continue;
                }
                feeShipReduce += feeShipReduce * (voucher.getReducePercent() / 100);
            }
        }
        return feeShipReduce;
    }

    @Transient
    private Long shopSubTotal;

    public Long getShopSubTotal() {
        if (shopItems == null || shopItems.isEmpty()) return 0L;
        return shopItems.stream()
                .map(ShopItem::getSubTotal)
                .reduce(0L, Long::sum);
    }
}
