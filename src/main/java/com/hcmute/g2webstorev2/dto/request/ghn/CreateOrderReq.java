package com.hcmute.g2webstorev2.dto.request.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.dto.response.ghn.CreateOrderItemData;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderReq {
    @JsonProperty("payment_type_id")
    private Integer paymentTypeId;
    private String note;
    @JsonProperty("required_note")
    private String requiredNote;
    @JsonProperty("return_phone")
    private String returnPhone;
    @JsonProperty("return_address")
    private String returnAddress;
    @JsonProperty("return_district_id")
    private Integer returnDistrictId;
    @JsonProperty("return_district_name")
    private String returnDistrictName;
    @JsonProperty("return_ward_code")
    private String returnWardCode;
    @JsonProperty("client_order_code")
    private String clientOrderCode;
    @JsonProperty("from_name")
    private String fromName;
    @JsonProperty("from_phone")
    private String fromPhone;
    @JsonProperty("from_address")
    private String fromAddress;
    @JsonProperty("from_ward_name")
    private String fromWardName;
    @JsonProperty("from_district_name")
    private String fromDistrictName;
    @JsonProperty("from_province_name")
    private String fromProvinceName;
    @JsonProperty("to_name")
    private String toName;
    @JsonProperty("to_phone")
    private String toPhone;
    @JsonProperty("to_address")
    private String toAddress;
    @JsonProperty("to_ward_name")
    private String toWardName;
    @JsonProperty("to_district_name")
    private String toDistrictName;
    @JsonProperty("to_province_name")
    private String toProvinceName;
    @JsonProperty("cod_amount")
    private Integer codAmount;
    private String content;
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    @JsonProperty("cod_failed_amount")
    private Integer codFailedAmount;
    @JsonProperty("pick_station_id")
    private Integer pickStationId;
    @JsonProperty("deliver_station_id")
    private Integer deliverStationId;
    @JsonProperty("insurance_value")
    private Integer insuranceValue;
    @JsonProperty("service_id")
    private Integer serviceId;
    @JsonProperty("service_type_id")
    private Integer serviceTypeId;
    private String coupon;
    @JsonProperty("pickup_time")
    private Integer pickupTime;
    @JsonProperty("pick_shift")
    private List<Integer> pickShift;
    private List<CreateOrderItemData> items;
}
