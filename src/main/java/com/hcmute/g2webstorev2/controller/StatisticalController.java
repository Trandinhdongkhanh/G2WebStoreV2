package com.hcmute.g2webstorev2.controller;

import com.hcmute.g2webstorev2.dto.response.StatisticalRes;
import com.hcmute.g2webstorev2.service.StatisticalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistical")
public class StatisticalController {
    @Autowired
    private StatisticalService statisticalService;
    @GetMapping
    @PreAuthorize("hasRole('SELLER_FULL_ACCESS')")
    public ResponseEntity<StatisticalRes> getStatistical(){
        return ResponseEntity.ok(statisticalService.getStatistical());
    }
}
