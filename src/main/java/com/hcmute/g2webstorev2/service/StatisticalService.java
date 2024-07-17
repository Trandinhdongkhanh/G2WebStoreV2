package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.response.AdminStatisticalRes;
import com.hcmute.g2webstorev2.dto.response.StatisticalRes;

public interface StatisticalService {
    StatisticalRes getStatistical(Integer year);

    AdminStatisticalRes getAdminStatistical();
}
