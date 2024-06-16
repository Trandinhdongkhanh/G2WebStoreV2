package com.hcmute.g2webstorev2.dto.response.ghn;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderItemData {
    private String name;
    private String code;
    private Integer quantity;
    private Integer price;
    private Integer length;
    private Integer width;
    private Integer weight;
    private Integer height;
}
