package com.hcmute.g2webstorev2.entity;

import com.hcmute.g2webstorev2.enums.OrderStatus;
import com.hcmute.g2webstorev2.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime createdDate;
    private LocalDateTime payedDate;
    private LocalDateTime deliveredDate;
    private LocalDate expectedDeliveryDate;
    private String ghnOrderCode;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    private Integer feeShip;
    private Integer feeShipReduce;
    private Integer g2VoucherPriceReduce;
    private Integer shopVoucherPriceReduce;
    private Integer pointSpent;
    private Integer total;
    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;
}