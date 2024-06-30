package com.hcmute.g2webstorev2.dto.request.zalopay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderReq {
    @JsonProperty("app_id")
    private Integer appId;
    @JsonProperty("app_user")
    private String appUser;
    @JsonProperty("app_trans_id")
    private String appTransId;
    @JsonProperty("app_time")
    private Long appTime;
    @JsonProperty("expire_duration_seconds")
    private Long expireDurationSeconds;
    @JsonProperty("amount")
    private Long amount;
    @JsonProperty("item")
    private List<ItemData> item;
    @JsonProperty("description")
    private String description;
    @JsonProperty("embed_data")
    private EmbedDataReq embedData;
    @JsonProperty("bank_code")
    private String bankCode;
    @JsonProperty("mac")
    private String mac;
    @JsonProperty("callback_url")
    private String callbackUrl;
    @JsonProperty("sub_app_id")
    private String subAppId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("email")
    private String email;
    @JsonProperty("address")
    private String address;
}
