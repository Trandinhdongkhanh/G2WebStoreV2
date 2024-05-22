package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.StatisticalRes;
import com.hcmute.g2webstorev2.service.StatisticalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistical")
@RequiredArgsConstructor
public class StatisticalController {
    private final StatisticalService statisticalService;

    @GetMapping
    @PreAuthorize("hasAnyRole(" +
            "'SELLER_FULL_ACCESS'," +
            "'SELLER_PROMOTION_ACCESS'," +
            "'SELLER_PRODUCT_ACCESS'," +
            "'JUNIOR_CHAT_AGENT'," +
            "'SELLER_ORDER_MANAGEMENT'," +
            "'SELLER_READ_ONLY')")
    public ResponseEntity<StatisticalRes> getStatistical(@RequestParam(value = "year", required = false) Integer year) {
        return ResponseEntity.ok(statisticalService.getStatistical(year));
    }
}
