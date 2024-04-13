package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneNoUpdateRequest {
    @JsonProperty("new_phone_no")
    @NotBlank(message = "New Phone No cannot be blank")
    private String newPhoneNo;
}
