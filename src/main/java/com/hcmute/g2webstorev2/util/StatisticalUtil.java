package com.hcmute.g2webstorev2.util;

import com.hcmute.g2webstorev2.entity.Order;
import com.hcmute.g2webstorev2.entity.OrderItem;
import com.hcmute.g2webstorev2.entity.Product;
import com.hcmute.g2webstorev2.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StatisticalUtil {
    private final OrderRepo orderRepo;

    private Long getProductIncomeByDay(Product product, LocalDate date) {
        List<Order> orders = orderRepo.getOrdersByShop(product.getShop(), date.atStartOfDay(), date.atTime(LocalTime.MAX));
        long income = 0;
        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProductId().equals(product.getProductId())) {
                    income += (long) orderItem.getPrice() * orderItem.getQuantity();
                }
            }
        }
        return income;
    }

    private Long getProductIncomeByMonth(Product product, LocalDate start, LocalDate end) {
        List<Order> orders = orderRepo.getOrdersByShop(product.getShop(), start.atStartOfDay(), end.atTime(LocalTime.MAX));
        long income = 0;
        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProductId().equals(product.getProductId())) {
                    income += (long) orderItem.getPrice() * orderItem.getQuantity();
                }
            }
        }
        return income;
    }

    private LocalDate getEndOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public Map<Month, Long> getMonthIncome(Product product) {
        int year = Year.now().getValue();

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

        Long januaryIncome = getProductIncomeByMonth(product, startJanuary, getEndOfMonth(startJanuary));
        Long februaryIncome = getProductIncomeByMonth(product, startFebruary, getEndOfMonth(startFebruary));
        Long marchIncome = getProductIncomeByMonth(product, startMarch, getEndOfMonth(startMarch));
        Long aprilIncome = getProductIncomeByMonth(product, startApril, getEndOfMonth(startApril));
        Long mayIncome = getProductIncomeByMonth(product, startMay, getEndOfMonth(startMay));
        Long juneIncome = getProductIncomeByMonth(product, startJune, getEndOfMonth(startJune));
        Long julyIncome = getProductIncomeByMonth(product, startJuly, getEndOfMonth(startJuly));
        Long augustIncome = getProductIncomeByMonth(product, startAugust, getEndOfMonth(startAugust));
        Long septemberIncome = getProductIncomeByMonth(product, startSeptember, getEndOfMonth(startSeptember));
        Long octoberIncome = getProductIncomeByMonth(product, startOctober, getEndOfMonth(startOctober));
        Long novemberIncome = getProductIncomeByMonth(product, startNovember, getEndOfMonth(startNovember));
        Long decemberIncome = getProductIncomeByMonth(product, startDecember, getEndOfMonth(startDecember));

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

        Map<Month, Long> map = new HashMap<>();
        map.put(Month.JANUARY, januaryIncome);
        map.put(Month.FEBRUARY, februaryIncome);
        map.put(Month.MARCH, marchIncome);
        map.put(Month.APRIL, aprilIncome);
        map.put(Month.MAY, mayIncome);
        map.put(Month.JUNE, juneIncome);
        map.put(Month.JULY, julyIncome);
        map.put(Month.AUGUST, augustIncome);
        map.put(Month.SEPTEMBER, septemberIncome);
        map.put(Month.OCTOBER, octoberIncome);
        map.put(Month.NOVEMBER, novemberIncome);
        map.put(Month.DECEMBER, decemberIncome);
        return map;
    }

    public Map<DayOfWeek, Long> getDayIncome(Product product, LocalDate today) {
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate tuesday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY));
        LocalDate wednesday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY));
        LocalDate thursday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY));
        LocalDate friday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY));
        LocalDate saturday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        LocalDate sunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        Long mondayIncome = getProductIncomeByDay(product, monday);
        Long tuesdayIncome = getProductIncomeByDay(product, tuesday);
        Long wednesdayIncome = getProductIncomeByDay(product, wednesday);
        Long thursdayIncome = getProductIncomeByDay(product, thursday);
        Long fridayIncome = getProductIncomeByDay(product, friday);
        Long saturdayIncome = getProductIncomeByDay(product, saturday);
        Long sundayIncome = getProductIncomeByDay(product, sunday);


        if (mondayIncome == null) mondayIncome = 0L;
        if (tuesdayIncome == null) tuesdayIncome = 0L;
        if (wednesdayIncome == null) wednesdayIncome = 0L;
        if (thursdayIncome == null) thursdayIncome = 0L;
        if (fridayIncome == null) fridayIncome = 0L;
        if (saturdayIncome == null) saturdayIncome = 0L;
        if (sundayIncome == null) sundayIncome = 0L;

        Map<DayOfWeek, Long> map = new HashMap<>();
        map.put(DayOfWeek.MONDAY, mondayIncome);
        map.put(DayOfWeek.TUESDAY, tuesdayIncome);
        map.put(DayOfWeek.WEDNESDAY, wednesdayIncome);
        map.put(DayOfWeek.THURSDAY, thursdayIncome);
        map.put(DayOfWeek.FRIDAY, fridayIncome);
        map.put(DayOfWeek.SATURDAY, saturdayIncome);
        map.put(DayOfWeek.SUNDAY, sundayIncome);
        return map;
    }
}
