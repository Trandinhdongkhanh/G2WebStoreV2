package com.hcmute.g2webstorev2.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderFeeData {
    @JsonProperty("main_service")
    private Integer mainService;
    private Integer insurance;
    @JsonProperty("cod_fee")
    private Integer codFee;
    @JsonProperty("station_do")
    private Integer stationDo;
    @JsonProperty("station_pu")
    private Integer stationPu;
    @JsonProperty("return")
    private Integer returnFee;
    private Integer r2s;
    @JsonProperty("return_again")
    private Integer returnAgain;
    private Integer coupon;
    @JsonProperty("document_return")
    private Integer documentReturn;
    @JsonProperty("double_check")
    private Integer doubleCheck;
    @JsonProperty("double_check_deliver")
    private Integer doubleCheckDeliver;
    @JsonProperty("pick_remote_areas_fee")
    private Integer pickRemoteAreasFee;
    @JsonProperty("deliver_remote_areas_fee")
    private Integer deliverRemoteAreasFee;
    @JsonProperty("pick_remote_areas_fee_return")
    private Integer pickRemoteAreasFeeReturn;
    @JsonProperty("deliver_remote_areas_fee_return")
    private Integer deliverRemoteAreasFeeReturn;
    @JsonProperty("cod_failed_fee")
    private Integer codFailedFee;
}

