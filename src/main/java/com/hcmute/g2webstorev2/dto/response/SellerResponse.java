package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.entity.Role;
import com.hcmute.g2webstorev2.entity.Shop;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerResponse {
    @JsonProperty("seller_id")
    private Integer sellerId;
    private String email;
    @JsonProperty("phone_no")
    private String phoneNo;
    @JsonProperty("full_name")
    private String fullName;
    private String avatar;
    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;
    private Role role;
    private Shop shop;
    @JsonProperty("is_main_acc")
    private boolean isMainAcc;

}
