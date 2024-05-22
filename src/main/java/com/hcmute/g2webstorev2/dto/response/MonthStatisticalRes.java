package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthStatisticalRes {
    private Long januaryIncome;
    private Long februaryIncome;
    private Long marchIncome;
    private Long aprilIncome;
    private Long mayIncome;
    private Long juneIncome;
    private Long julyIncome;
    private Long augustIncome;
    private Long septemberIncome;
    private Long octoberIncome;
    private Long novemberIncome;
    private Long decemberIncome;
}
