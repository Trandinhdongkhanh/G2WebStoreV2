package com.hcmute.g2webstorev2.service.impl;

import com.hcmute.g2webstorev2.repository.NotificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl {
    private final NotificationRepo notificationRepo;
}
