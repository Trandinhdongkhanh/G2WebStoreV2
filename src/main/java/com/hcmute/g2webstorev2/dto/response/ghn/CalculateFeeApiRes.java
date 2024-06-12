package com.hcmute.g2webstorev2.dto.response.ghn;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculateFeeApiRes {
    private Integer code;
    private String message;
    private FeeShipRes data;
}
