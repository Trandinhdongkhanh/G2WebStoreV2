package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "cartItemV2")
    private Set<ShopItem> shopItems;
    @ManyToMany
    @JoinTable(
            name = "cart_item_voucher",
            joinColumns = @JoinColumn(name = "cart_item_v2_id"),
            inverseJoinColumns = @JoinColumn(name = "voucher_id")
    )
    private Set<Voucher> vouchers;
    public Long shopSubTotal;
}
