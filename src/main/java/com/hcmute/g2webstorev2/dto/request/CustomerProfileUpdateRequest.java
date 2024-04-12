package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerProfileUpdateRequest {
    private String avatar;
    private LocalDate dob;
    @Email(message = "Email is invalid",
            regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(.[A-Za-z0-9_-]+)*@"
                    + "[^-][A-Za-z0-9-]+(.[A-Za-z0-9-]+)*(.[A-Za-z]{2,})$")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @JsonProperty("full_name")
    private String fullName;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 3, message = "Password must have at least 3 characters")
    private String password;
    @JsonProperty("phone_no")
    private String phoneNo;
}
