package com.hcmute.g2webstorev2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class G2WebStoreV2Application {

    public static void main(String[] args) {
        SpringApplication.run(G2WebStoreV2Application.class, args);
    }
}
