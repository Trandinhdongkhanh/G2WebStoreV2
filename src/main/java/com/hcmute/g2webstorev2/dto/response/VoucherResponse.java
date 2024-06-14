package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.DiscountType;
import com.hcmute.g2webstorev2.enums.VoucherType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherResponse {
    private String id;
    private String name;
    @JsonProperty("start_date")
    private LocalDateTime startDate;
    @JsonProperty("end_date")
    private LocalDateTime endDate;
    @JsonProperty("discount_type")
    private DiscountType discountType;
    @JsonProperty("voucher_type")
    private VoucherType voucherType;
    @JsonProperty("min_spend")
    private Integer minSpend;
    @JsonProperty("reduce_price")
    private Integer reducePrice;
    @JsonProperty("reduce_percent")
    private Integer reducePercent;
    private Integer quantity;
    @JsonProperty("use_count")
    private Integer useCount;
    @JsonProperty("shop_id")
    private Integer shopId;
    @JsonProperty("is_paused")
    private Boolean isPaused;
}
