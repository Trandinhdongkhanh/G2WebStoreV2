package com.hcmute.g2webstorev2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.AppPermission;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "permission")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    @JsonProperty("permission_id")
    private Integer permissionId;
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    @JsonProperty("app_permission")
    private AppPermission appPermission;
    public Permission(AppPermission appPermission){
        this.appPermission = appPermission;
    }
}
