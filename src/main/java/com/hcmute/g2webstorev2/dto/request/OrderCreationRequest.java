package com.hcmute.g2webstorev2.dto.request;

import com.hcmute.g2webstorev2.dto.response.CartItemResponse;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreationRequest {
    @NotNull(message = "Items cannot be null")
    private List<CartItemResponse> items;
}
