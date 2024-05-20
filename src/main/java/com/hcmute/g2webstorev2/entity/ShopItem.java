package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "shop_item")
public class ShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_item_id")
    private Long shopItemId;
    @ManyToOne
    @JoinColumn(name = "cart_item_v2_id")
    private CartItemV2 cartItemV2;
    private Long price;
    private String name;
    private Integer quantity;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;
    @Transient
    private Long subTotal;
    public Long getSubTotal() {
        return (long) product.getPrice() * quantity;
    }
}
