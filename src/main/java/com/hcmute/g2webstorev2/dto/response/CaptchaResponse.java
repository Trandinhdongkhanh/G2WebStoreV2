package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaptchaResponse {
    private boolean success;
    private String challenge_ts;
    private String hostname;
}
