package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.DiscountType;
import com.hcmute.g2webstorev2.enums.VoucherType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherResponse {
    private String id;
    private String name;
    @JsonProperty("start_date")
    private LocalDate startDate;
    @JsonProperty("end_date")
    private LocalDate endDate;
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
    @JsonProperty("max_use_per_cus")
    private Integer maxUsePerCus;
    @JsonProperty("shop_id")
    private Integer shopId;
}
