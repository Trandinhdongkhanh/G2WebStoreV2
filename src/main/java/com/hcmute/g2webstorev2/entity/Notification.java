package com.hcmute.g2webstorev2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notification")
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;
    private String content;
    private LocalDateTime createdDate;
    private Boolean isRead;
    private Boolean isPublic;
    private Integer customerId;
    private Integer sellerId;
}
