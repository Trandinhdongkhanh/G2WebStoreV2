package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerAddRequest {
    @Email(message = "Email is invalid",
            regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(.[A-Za-z0-9_-]+)*@"
                    + "[^-][A-Za-z0-9-]+(.[A-Za-z0-9-]+)*(.[A-Za-z]{2,})$")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 3, message = "Password must have at least 3 characters")
    private String password;
    @JsonProperty("role_id")
    @NotNull(message = "Role ID must not be null")
    @Min(value = 1, message = "Role ID must not be less than 1")
    private Integer roleId;
}
