package com.hcmute.g2webstorev2.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerProfileUpdateRequest {
    private String password;
}
