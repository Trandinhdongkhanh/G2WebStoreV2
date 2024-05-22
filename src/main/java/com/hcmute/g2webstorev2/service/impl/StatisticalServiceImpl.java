package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.response.DayStatisticalRes;
import com.hcmute.g2webstorev2.dto.response.MonthStatisticalRes;
import com.hcmute.g2webstorev2.dto.response.StatisticalRes;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.repository.OrderItemRepo;
import com.hcmute.g2webstorev2.repository.OrderRepo;
import com.hcmute.g2webstorev2.repository.ProductRepo;
import com.hcmute.g2webstorev2.service.StatisticalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;


@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticalServiceImpl implements StatisticalService {
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;

    @Override
    public StatisticalRes getStatistical(Integer year) {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer shopId = seller.getShop().getShopId();

        long unReviewedOrderCount = orderItemRepo.countUnReviewedItems(seller.getShop().getShopId());
        long deliveringOrderCount = orderRepo.countDeliveringOrder(seller.getShop().getShopId());
        long unHandledOrderCount = orderRepo.countUnHandledOrder(seller.getShop().getShopId());
        long successOrderCount = orderRepo.countSuccessOrder(seller.getShop().getShopId());
        long canceledOrderCount = orderRepo.countCanceledOrder(seller.getShop().getShopId());
        long onSaleProductCount = productRepo.countOnSaleProduct(seller.getShop().getShopId());
        long outOfStockProductCount = productRepo.countOutOfStockProduct(seller.getShop().getShopId());

        return StatisticalRes.builder()
                .dayStatistical(getDayStatistical(LocalDate.now(), shopId))
                .monthStatisticalRes(getMonthStatistical(year, shopId))
                .unReviewedOrderCount(unReviewedOrderCount)
                .onDeliveredOrderCount(deliveringOrderCount)
                .unHandledOrderCount(unHandledOrderCount)
                .successOrderCount(successOrderCount)
                .canceledOrderCount(canceledOrderCount)
                .onSaleProductCount(onSaleProductCount)
                .outOfStockProductCount(outOfStockProductCount)
                .build();
    }

    private Long getShopIncome(Integer shopId, LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        return orderRepo.getShopIncome(shopId, firstDayOfMonth.atStartOfDay(), lastDayOfMonth.atTime(LocalTime.MAX));
    }

    private Long getShopIncome(Integer shopId, LocalDate date) {
        return orderRepo.getShopIncome(shopId, date.atStartOfDay(), date.atTime(LocalTime.MAX));
    }

    private LocalDate getEndOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    private MonthStatisticalRes getMonthStatistical(Integer year, Integer shopId) {
        if (year == null) year = Year.now().getValue();

        LocalDate startJanuary = LocalDate.of(year, Month.JANUARY, 1);
        LocalDate startFebruary = LocalDate.of(year, Month.FEBRUARY, 1);
        LocalDate startMarch = LocalDate.of(year, Month.MARCH, 1);
        LocalDate startApril = LocalDate.of(year, Month.APRIL, 1);
        LocalDate startMay = LocalDate.of(year, Month.MAY, 1);
        LocalDate startJune = LocalDate.of(year, Month.JUNE, 1);
        LocalDate startJuly = LocalDate.of(year, Month.JULY, 1);
        LocalDate startAugust = LocalDate.of(year, Month.AUGUST, 1);
        LocalDate startSeptember = LocalDate.of(year, Month.SEPTEMBER, 1);
        LocalDate startOctober = LocalDate.of(year, Month.OCTOBER, 1);
        LocalDate startNovember = LocalDate.of(year, Month.NOVEMBER, 1);
        LocalDate startDecember = LocalDate.of(year, Month.DECEMBER, 1);

        Long januaryIncome = getShopIncome(shopId, startJanuary, getEndOfMonth(startJanuary));
        Long februaryIncome = getShopIncome(shopId, startFebruary, getEndOfMonth(startFebruary));
        Long marchIncome = getShopIncome(shopId, startMarch, getEndOfMonth(startMarch));
        Long aprilIncome = getShopIncome(shopId, startApril, getEndOfMonth(startApril));
        Long mayIncome = getShopIncome(shopId, startMay, getEndOfMonth(startMay));
        Long juneIncome = getShopIncome(shopId, startJune, getEndOfMonth(startJune));
        Long julyIncome = getShopIncome(shopId, startJuly, getEndOfMonth(startJuly));
        Long augustIncome = getShopIncome(shopId, startAugust, getEndOfMonth(startAugust));
        Long septemberIncome = getShopIncome(shopId, startSeptember, getEndOfMonth(startSeptember));
        Long octoberIncome = getShopIncome(shopId, startOctober, getEndOfMonth(startOctober));
        Long novemberIncome = getShopIncome(shopId, startNovember, getEndOfMonth(startNovember));
        Long decemberIncome = getShopIncome(shopId, startDecember, getEndOfMonth(startDecember));

        if (januaryIncome == null) januaryIncome = 0L;
        if (februaryIncome == null) februaryIncome = 0L;
        if (marchIncome == null) marchIncome = 0L;
        if (aprilIncome == null) aprilIncome = 0L;
        if (mayIncome == null) mayIncome = 0L;
        if (juneIncome == null) juneIncome = 0L;
        if (julyIncome == null) julyIncome = 0L;
        if (augustIncome == null) augustIncome = 0L;
        if (septemberIncome == null) septemberIncome = 0L;
        if (octoberIncome == null) octoberIncome = 0L;
        if (novemberIncome == null) novemberIncome = 0L;
        if (decemberIncome == null) decemberIncome = 0L;

        return MonthStatisticalRes.builder()
                .januaryIncome(januaryIncome)
                .februaryIncome(februaryIncome)
                .marchIncome(marchIncome)
                .aprilIncome(aprilIncome)
                .mayIncome(mayIncome)
                .juneIncome(juneIncome)
                .julyIncome(julyIncome)
                .augustIncome(augustIncome)
                .septemberIncome(septemberIncome)
                .octoberIncome(octoberIncome)
                .novemberIncome(novemberIncome)
                .decemberIncome(decemberIncome)
                .build();
    }

    private DayStatisticalRes getDayStatistical(LocalDate today, Integer shopId) {
        LocalDate startMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startTuesday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY));
        LocalDate startWednesday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY));
        LocalDate startThursday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY));
        LocalDate startFriday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY));
        LocalDate startSaturday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        LocalDate startSunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        Long mondayIncome = getShopIncome(shopId, startMonday);
        Long tuesdayIncome = getShopIncome(shopId, startTuesday);
        Long wednesdayIncome = getShopIncome(shopId, startWednesday);
        Long thursdayIncome = getShopIncome(shopId, startThursday);
        Long fridayIncome = getShopIncome(shopId, startFriday);
        Long saturdayIncome = getShopIncome(shopId, startSaturday);
        Long sundayIncome = getShopIncome(shopId, startSunday);


        if (mondayIncome == null) mondayIncome = 0L;
        if (tuesdayIncome == null) tuesdayIncome = 0L;
        if (wednesdayIncome == null) wednesdayIncome = 0L;
        if (thursdayIncome == null) thursdayIncome = 0L;
        if (fridayIncome == null) fridayIncome = 0L;
        if (saturdayIncome == null) saturdayIncome = 0L;
        if (sundayIncome == null) sundayIncome = 0L;

        return DayStatisticalRes.builder()
                .mondayIncome(mondayIncome)
                .tuesdayIncome(tuesdayIncome)
                .wednesdayIncome(wednesdayIncome)
                .thursdayIncome(thursdayIncome)
                .fridayIncome(fridayIncome)
                .saturdayIncome(saturdayIncome)
                .sundayIncome(sundayIncome)
                .build();
    }
}