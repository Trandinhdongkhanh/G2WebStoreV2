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
    @NotBlank(message = "Image cannot be blank")
    private String image;
    @NotBlank(message = "Shop name cannot be blank")
    private String name;
    @NotBlank(message = "Province cannot be blank")
    private String province;
    @NotBlank(message = "District cannot be blank")
    private String district;
    @JsonProperty("district_id")
    @NotNull(message = "District ID cannot be null")
    @Min(value = 1, message = "District ID must be equals or greater than 1")
    private Integer districtId;
    @NotBlank(message = "Ward cannot be blank")
    private String ward;
    @NotBlank(message = "Street cannot be blank")
    private String street;
}
