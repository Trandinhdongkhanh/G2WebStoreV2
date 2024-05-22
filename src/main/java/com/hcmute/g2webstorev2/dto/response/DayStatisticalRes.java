package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DayStatisticalRes {
    private Long mondayIncome;
    private Long tuesdayIncome;
    private Long wednesdayIncome;
    private Long thursdayIncome;
    private Long fridayIncome;
    private Long saturdayIncome;
    private Long sundayIncome;
}
