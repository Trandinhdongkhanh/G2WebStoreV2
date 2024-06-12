package com.hcmute.g2webstorev2.dto.request.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeeShipReq {
    @JsonProperty("service_id")
    private Integer serviceId;
    @JsonProperty("service_type_id")
    private Integer serviceTypeId;
    @JsonProperty("insurance_value")
    private Integer insuranceValue;
    private String coupon;
    @JsonProperty("cod_failed_amount")
    private Integer codFailedAmount;
    @JsonProperty("from_district_id")
    private Integer fromDistrictId;
    @JsonProperty("from_ward_code")
    private String fromWardCode;
    @JsonProperty("to_ward_code")
    private String toWardCode;
    @JsonProperty("to_district_id")
    private Integer toDistrictId;
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    @JsonProperty("cod_value")
    private Integer codValue;
}
