package com.hcmute.g2webstorev2.dto.response.ghn;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintOrderApiRes {
    private Integer code;
    private String message;
    private TokenData data;
}
