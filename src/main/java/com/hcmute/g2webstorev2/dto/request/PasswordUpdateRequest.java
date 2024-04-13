package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordUpdateRequest {
    @JsonProperty("old_password")
    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;
    @JsonProperty("new_password")
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 3, message = "New password must have above 3 characters")
    private String newPassword;
}
