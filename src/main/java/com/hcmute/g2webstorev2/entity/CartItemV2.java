package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "cartItemV2")
    private List<CartItemVoucher> cartItemVouchers;
    @Transient
    private Long shopReduce;

    public Long getShopReduce() {
        if (cartItemVouchers == null || cartItemVouchers.isEmpty()) return 0L;
        float shopReduce = 0L;
        float shopSubTotal = (float) getShopSubTotal();
        for (CartItemVoucher cartItemVoucher : cartItemVouchers) {
            if (cartItemVoucher.getIsSelected() && cartItemVoucher.getIsEligible()) {
                Voucher voucher = cartItemVoucher.getVoucher();
                if (Objects.equals(voucher.getVoucherType(), SHOP_VOUCHER)) {
                    if (voucher.getDiscountType().equals(PRICE))
                        shopReduce += voucher.getReducePrice();
                    else if (voucher.getDiscountType().equals(PERCENTAGE))
                        shopReduce += shopSubTotal * ((float) voucher.getReducePercent() / 100);
                }
            }
        }
        return (long) Math.min(shopReduce, shopSubTotal);
    }

    @Transient
    private Long feeShipReduce;

    public Long getFeeShipReduce() {
        if (cartItemVouchers == null || cartItemVouchers.isEmpty()) return 0L;
        long feeShipReduce = 0L;
        for (CartItemVoucher cartItemVoucher : cartItemVouchers) {
            if (cartItemVoucher.getIsSelected() && cartItemVoucher.getIsEligible()) {
                Voucher voucher = cartItemVoucher.getVoucher();
                if (Objects.equals(voucher.getVoucherType(), FREE_SHIP_VOUCHER)) {
                    if (Objects.equals(voucher.getDiscountType(), PRICE)) feeShipReduce += voucher.getReducePrice();
                }
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
