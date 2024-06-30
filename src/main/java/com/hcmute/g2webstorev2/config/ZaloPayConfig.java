package com.hcmute.g2webstorev2.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ZaloPayConfig {
    @Value("${zalopay.app-id}")
    private Integer appId;
    @Value("${zalopay.key1}")
    private String key1;
    @Value("${zalopay.key2}")
    private String key2;
    @Value("${zalopay.api.create-order}")
    private String createOrderApi;
    @Value("${zalopay.callback-url}")
    private String callBackUrl;
}
