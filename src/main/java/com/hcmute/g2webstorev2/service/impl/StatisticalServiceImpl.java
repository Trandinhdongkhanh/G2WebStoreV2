package com.hcmute.g2webstorev2.service.impl;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;


@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticalServiceImpl implements StatisticalService {
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;

    @Override
    public StatisticalRes getStatistical() {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        LocalDateTime startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);

        LocalDateTime startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        Long dayIncome = orderRepo.getShopIncome(seller.getShop().getShopId(), startOfDay, endOfDay);
        Long weekIncome = orderRepo.getShopIncome(seller.getShop().getShopId(), startOfWeek, endOfWeek);
        Long monthIncome = orderRepo.getShopIncome(seller.getShop().getShopId(), startOfMonth, endOfMonth);
        if (dayIncome == null) dayIncome = 0L;
        if (weekIncome == null) weekIncome = 0L;
        if (monthIncome == null) monthIncome = 0L;

        long unReviewedOrderCount = orderItemRepo.countUnReviewedItems(seller.getShop().getShopId());
        long deliveringOrderCount = orderRepo.countDeliveringOrder(seller.getShop().getShopId());
        long unHandledOrderCount = orderRepo.countUnHandledOrder(seller.getShop().getShopId());
        long successOrderCount = orderRepo.countSuccessOrder(seller.getShop().getShopId());
        long canceledOrderCount = orderRepo.countCanceledOrder(seller.getShop().getShopId());
        long onSaleProductCount = productRepo.countOnSaleProduct(seller.getShop().getShopId());
        long outOfStockProductCount = productRepo.countOutOfStockProduct(seller.getShop().getShopId());

        return StatisticalRes.builder()
                .dayIncome(dayIncome)
                .weekIncome(weekIncome)
                .monthIncome(monthIncome)
                .unHandledOrderCount(unHandledOrderCount)
                .onDeliveredOrderCount(deliveringOrderCount)
                .successOrderCount(successOrderCount)
                .unReviewedOrderCount(unReviewedOrderCount)
                .canceledOrderCount(canceledOrderCount)
                .onSaleProductCount(onSaleProductCount)
                .outOfStockProductCount(outOfStockProductCount)
                .build();
    }
}
