package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerProductCompositeKey implements Serializable {
    @Column(name = "customer_id")
    private Integer customerId;
    @Column(name = "product_id")
    private Integer productId;
}
