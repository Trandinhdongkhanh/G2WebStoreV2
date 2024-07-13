package com.hcmute.g2webstorev2.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReviewUtil {
    public static double getAvgRate(Long totalRateValue, Long totalRateCount) {
        if (totalRateValue == null) totalRateValue = 0L;
        double avgRate = 0.0;
        if (totalRateCount != 0) avgRate = (double) totalRateValue / totalRateCount;
        return Math.round(avgRate * 10.0) / 10.0; // làm tròn đến 1 chữ số thập phân
    }
}
