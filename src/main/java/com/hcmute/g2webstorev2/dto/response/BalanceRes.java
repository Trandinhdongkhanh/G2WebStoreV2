package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceRes {
    private Long shopBalance;
    private String bankAccSeriesNum;
    private String bankName;
    private String bankAccHolderName;
}
