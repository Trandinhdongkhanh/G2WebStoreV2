package com.hcmute.g2webstorev2.entity;

import com.hcmute.g2webstorev2.entity.composite_key.CartItemVoucherKey;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_item_voucher")
@Entity
@Builder
public class CartItemVoucher {
    @EmbeddedId
    private CartItemVoucherKey key;
    @ManyToOne
    @MapsId("voucherId")
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
    @ManyToOne
    @MapsId("cartItemV2Id")
    @JoinColumn(name = "cart_item_v2_id")
    private CartItemV2 cartItemV2;
    private Boolean isSelected;
    private Boolean isEligible;
}
