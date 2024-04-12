package com.hcmute.g2webstorev2.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    @JsonProperty("address_id")
    private Integer addressId;
    private String street;
    private String ward;
    @JsonProperty("district_id")
    private Integer districtId;
    private String district;
    private String province;
    @JsonProperty("customer_id")
    private Integer customerId;
}
