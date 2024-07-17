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
    @JsonProperty("province_id")
    private Integer provinceId;
    @JsonProperty("province_name")
    private String provinceName;
    @JsonProperty("district_id")
    private Integer districtId;
    @JsonProperty("district_name")
    private String districtName;
    @JsonProperty("ward_code")
    private String wardCode;
    @JsonProperty("ward_name")
    private String wardName;
    private String street;
    private Long balance;
    @JsonProperty("is_allowed_to_sell")
    private Boolean isAllowedToSell;
    @JsonProperty("violation_point")
    private Integer violationPoint;
    @JsonProperty("bank_acc_holder_name")
    private String bankAccHolderName;
    @JsonProperty("bank_acc_series_num")
    private String bankAccSeriesNum;
    @JsonProperty("bank_name")
    private String bankName;
}
