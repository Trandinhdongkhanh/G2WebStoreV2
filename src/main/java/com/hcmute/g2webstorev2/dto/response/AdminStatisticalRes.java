package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminStatisticalRes {
    @JsonProperty("customer_count")
    private Long customerCount;
    @JsonProperty("shop_count")
    private Long shopCount;
    private Double income;
    @JsonProperty("today_cus_count")
    private Long todayCusCount;
    @JsonProperty("today_shop_count")
    private Long todayShopCount;
    @JsonProperty("cus_month_res")
    private CustomerMonthRes cusMonthRes;
    @JsonProperty("shop_month_res")
    private ShopMonthRes shopMonthRes;
}
