package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    List<ShopResponse> shops;

}
