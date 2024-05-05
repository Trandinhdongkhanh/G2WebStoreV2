package com.hcmute.g2webstorev2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmute.g2webstorev2.enums.PaymentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersCreationRequest {
    private List<OrderCreationRequest> orders;
    @JsonProperty("address_id")
    @NotNull(message = "Address ID must not be null")
    @Min(value = 1, message = "Address ID must not be less than 1")
    private Integer addressId;
    @JsonProperty("payment_type")
    private PaymentType paymentType;
    @JsonProperty("is_point_spent")
    private Boolean isPointSpent;
}
