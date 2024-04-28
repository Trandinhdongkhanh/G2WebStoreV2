package com.hcmute.g2webstorev2.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class VNPAYConfig {
    @Value("${vnp.pay-url}")
    private String vnp_PayUrl;
    @Value("${vnp.return-url}")
    private String vnp_ReturnUrl;
    @Value("${vnp.tmn-code}")
    private String vnp_TmnCode;
    @Value("${vnp.secret-key}")
    private String secretKey;
    @Value("${vnp.api-url}")
    private String vnp_ApiUrl;
    @Value("${vnp.version}")
    private String vnp_Version;
}
