package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.DiscountType;
import com.hcmute.g2webstorev2.enums.VoucherType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherRequest {
    @NotBlank(message = "Voucher name cannot be blank")
    private String name;
    @JsonProperty("start_date")
    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;
    @JsonProperty("end_date")
    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;
    @JsonProperty("discount_type")
    @NotNull(message = "Discount type cannot be null")
    private DiscountType discountType;
    @JsonProperty("voucher_type")
    @NotNull(message = "Voucher type cannot be null")
    private VoucherType voucherType;
    @JsonProperty("min_spend")
    @NotNull(message = "Min spend cannot be null")
    @Min(value = 0, message = "Min spend must be equals or greater than 0")
    private Integer minSpend;
    @JsonProperty("reduce_price")
    @Min(value = 0, message = "Reduce price must be equals or greater than 0")
    private Integer reducePrice;
    @JsonProperty("reduce_percent")
    @Min(value = 0, message = "Reduce percent must be equals or greater than 0")
    @Max(value = 100, message = "Reduce percent must be equals or smaller than 100")
    private Integer reducePercent;
    @Min(value = 1, message = "Quantity must be equals or greater than 1")
    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}
