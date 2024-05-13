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
    private Long successOrderCount;
    private Long onDeliveredOrderCount;
    private Long unHandledOrderCount;
    private Long canceledOrderCount;
    private Long onSaleProductCount;
    private Long outOfStockProductCount;
    private Long unReviewedOrderCount;
}
