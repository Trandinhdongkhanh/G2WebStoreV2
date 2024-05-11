package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.dto.response.StatisticalRes;
import com.hcmute.g2webstorev2.entity.Seller;
import com.hcmute.g2webstorev2.service.StatisticalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class StatisticalServiceImpl implements StatisticalService {
    @Override
    public StatisticalRes getStatistical() {
        Seller seller = (Seller) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return null;
    }
}
