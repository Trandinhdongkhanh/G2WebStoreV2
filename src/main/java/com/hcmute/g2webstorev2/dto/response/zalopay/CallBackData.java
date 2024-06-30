package com.hcmute.g2webstorev2.dto.response.zalopay;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.dto.request.zalopay.EmbedDataReq;
import com.hcmute.g2webstorev2.dto.request.zalopay.ItemData;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallBackData {
    @JsonProperty("app_id")
    private Integer appId;
    @JsonProperty("app_trans_id")
    private String appTransId;
    @JsonProperty("app_time")
    private Long appTime;
    @JsonProperty("app_user")
    private String appUser;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("embed_data")
    private EmbedDataReq embedData;
    @JsonProperty("item")
    private List<ItemData> item;
    @JsonProperty("zp_trans_id")
    private Long zpTransId;
    @JsonProperty("server_time")
    private Long serverTime;
    @JsonProperty("channel")
    private Integer channel;
    @JsonProperty("merchant_user_id")
    private String merchantUserId;
    @JsonProperty("user_fee_amount")
    private Long userFeeAmount;
    @JsonProperty("discount_amount")
    private Long discountAmount;
}
