package com.hcmute.g2webstorev2.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticalRes {
    private DayStatisticalRes dayStatistical;
    private MonthStatisticalRes monthStatisticalRes;
    private Long successOrderCount;
    private Long onDeliveredOrderCount;
    private Long unHandledOrderCount;
    private Long canceledOrderCount;
    private Long onSaleProductCount;
    private Long outOfStockProductCount;
    private Long unReviewedOrderCount;
}
