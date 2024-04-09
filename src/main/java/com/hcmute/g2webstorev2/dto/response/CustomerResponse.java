package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.entity.Role;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    @JsonProperty("customer_id")
    private Integer customerId;
    private String email;
    @JsonProperty("phone_no")
    private String phoneNo;

    @JsonProperty("full_name")
    private String fullName;

    private String avatar;
    private float point;
    private LocalDate dob;
    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;;
    private Role role;
}
