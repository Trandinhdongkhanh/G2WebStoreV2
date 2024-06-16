package com.hcmute.g2webstorev2.dto.request.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpectedDeliveryDateReq {
    @JsonProperty("from_district_id")
    @NotNull(message = "From district ID must not be null")
    private Integer fromDistrictId;
    @JsonProperty("from_ward_code")
    @NotBlank(message = "From ward code must not be blank")
    private String fromWardCode;
    @JsonProperty("to_district_id")
    @NotNull(message = "To district ID must not be null")
    private Integer toDistrictId;
    @JsonProperty("to_ward_code")
    @NotBlank(message = "To ward code must not be blank")
    private String toWardCode;
    @JsonProperty("service_id")
    @NotNull(message = "Service ID must not be null")
    private Integer serviceId;
}
