package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequest {
    private String street;
    private String ward;
    @JsonProperty("district_id")
    private Integer districtId;
    private String district;
    private String province;
    @JsonProperty("customer_id")
    private Integer customerId;
}
