package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopIncomeRes {
    private Long dayIncome;
    private Long weekIncome;
    private Long monthIncome;
}
