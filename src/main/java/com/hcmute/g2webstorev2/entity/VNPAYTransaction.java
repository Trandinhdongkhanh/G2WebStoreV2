package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "vnpay_transaction")
public class VNPAYTransaction {
    @Id
    private String vnp_TxnRef;

}
