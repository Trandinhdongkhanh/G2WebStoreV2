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
    @JsonProperty("order_receive_address")
    private String orderReceiveAddress;
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
    @JsonProperty("receiver_name")
    @NotBlank(message = "Receiver name cannot be blank")
    private String receiverName;
    @JsonProperty("receiver_phone_no")
    @NotBlank(message = "Receiver phone no cannot be blank")
    private String receiverPhoneNo;
    @JsonProperty("is_default")
    private boolean isDefault;
}
