package com.hcmute.g2webstorev2.dto.response.ghn;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpectedDeliveryDateApiRes {
    private Integer code;
    private String message;
    private ExpectedDeliverDateData data;
}
