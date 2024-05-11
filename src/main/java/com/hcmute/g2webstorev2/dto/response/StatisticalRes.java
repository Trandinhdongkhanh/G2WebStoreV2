package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticalRes {
    private Long dayIncome;
    private Long weekIncome;
    private Long monthIncome;
    private Integer unHandledOrderCount;
    private Integer onDeliveredOrderCount;
    private Integer successOrderCount;
    private Integer unReviewedOrderCount;
}
