package com.hcmute.g2webstorev2.entity.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemVoucherKey implements Serializable {
    @Column(name = "cart_item_v2_id")
    private Long cartItemV2Id;
    @Column(name = "voucher_id")
    private String voucherId;
}
