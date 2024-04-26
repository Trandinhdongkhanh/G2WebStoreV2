package com.hcmute.g2webstorev2.util;

import com.hcmute.g2webstorev2.dto.response.CaptchaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class CaptchaValidator {
    @Value("${captcha.secret}")
    private String secret;
    @Value("${captcha.url}")
    private String baseUrl;

    public boolean isValidCaptcha(String captcha) {
        RestTemplate restTemplate = new RestTemplate();
        String params = "?secret=" + secret + "&response=" + captcha;
        String completeUrl = baseUrl + params;
        CaptchaResponse res = restTemplate.postForObject(completeUrl, null, CaptchaResponse.class);
        return res.isSuccess();
    }
}
