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
public class AddressRequest {
    @NotBlank(message = "Street cannot be blank")
    private String street;
    @NotBlank(message = "Ward cannot be blank")
    private String ward;
    @JsonProperty("district_id")
    @NotNull(message = "District ID cannot be null")
    @Min(value = 1, message = "District ID must be equals or greater than 1")
    private Integer districtId;
    @NotBlank(message = "District cannot be blank")
    private String district;
    @NotBlank(message = "Province cannot be blank")
    private String province;
    @JsonProperty("customer_id")
    @NotNull(message = "Customer ID cannot be null")
    @Min(value = 1, message = "Customer ID must be equals or greater than 1")
    private Integer customerId;
}
