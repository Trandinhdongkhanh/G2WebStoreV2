package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {
    @NotBlank(message = "Content cannot be blank")
    private String content;
    @Min(value = 1, message = "Star rate must be greater than 0")
    @Max(value = 5, message = "Star rate must be smaller than 6")
    @NotNull(message = "Star rate cannot be null")
    private Integer rate;
    @NotNull(message = "Order Item ID cannot be null")
    @Min(value = 1, message = "Product ID must be greater than 0")
    private Integer orderItemId;
}
