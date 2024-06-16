package com.hcmute.g2webstorev2.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderResData {
    @JsonProperty("order_code")
    private String orderCode;
    @JsonProperty("sort_code")
    private String sortCode;
    @JsonProperty("trans_type")
    private String transType;
    @JsonProperty("ward_encode")
    private String wardEncode;
    @JsonProperty("district_encode")
    private String districtEncode;
    private CreateOrderFeeData fee;
    @JsonProperty("total_fee")
    private Integer totalFee;
    @JsonProperty("expected_delivery_time")
    private String expectedDeliveryTime;
    @JsonProperty("operation_partner")
    private String operationPartner;
}
