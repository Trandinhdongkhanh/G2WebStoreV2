package com.hcmute.g2webstorev2.entity;

import com.hcmute.g2webstorev2.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

}
