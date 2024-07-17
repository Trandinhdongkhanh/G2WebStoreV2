package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopRequest {
    @NotBlank(message = "Shop name cannot be blank")
    private String name;
    @JsonProperty("province_id")
    @NotNull(message = "Province ID must not be null")
    private Integer provinceId;
    @NotBlank(message = "Province name cannot be blank")
    @JsonProperty("province_name")
    private String provinceName;
    @JsonProperty("district_id")
    @NotNull(message = "District ID cannot be null")
    @Min(value = 1, message = "District ID must be equals or greater than 1")
    private Integer districtId;
    @NotBlank(message = "District name cannot be blank")
    @JsonProperty("district_name")
    private String districtName;
    @NotBlank(message = "Ward code must not be blank")
    @NotNull(message = "Ward code must not be null")
    @JsonProperty("ward_code")
    private String wardCode;
    @NotBlank(message = "Ward name cannot be blank")
    @JsonProperty("ward_name")
    private String wardName;
    @NotBlank(message = "Street cannot be blank")
    private String street;
    @JsonProperty("bank_acc_holder_name")
    private String bankAccHolderName;
    @JsonProperty("bank_name")
    private String bankName;
    @JsonProperty("bank_acc_series_num")
    private String bankAccSeriesNum;
}
