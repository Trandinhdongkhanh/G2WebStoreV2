package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.AppRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellersFromShopResponse {
    @JsonProperty("seller_id")
    private Integer sellerId;
    @JsonProperty("shop_id")
    private Integer shopId;
    private String email;
    private AppRole role;
    @JsonProperty("role_id")
    private Integer roleId;
    @JsonProperty("is_enabled")
    private boolean isEnabled;
    @JsonProperty("is_main_acc")
    private boolean isMainAcc;
    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;
}
