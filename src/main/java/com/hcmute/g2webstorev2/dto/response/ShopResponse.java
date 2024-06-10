package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopResponse {
    @JsonProperty("shop_id")
    private Integer shopId;
    private String image;
    private String name;
    private String province;
    private String district;
    @JsonProperty("district_id")
    private Integer districtId;
    private String ward;
    private String street;
    private Long balance;
    @JsonProperty("is_allowed_to_sell")
    private Boolean isAllowedToSell;
    @JsonProperty("violation_point")
    private Integer violationPoint;
}
