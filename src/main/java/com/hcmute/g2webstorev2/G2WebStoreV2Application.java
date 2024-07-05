package com.hcmute.g2webstorev2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJpaRepositories(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = JpaRepository.class))
//@EnableElasticsearchRepositories(
//        includeFilters = @ComponentScan.Filter(
//                type = FilterType.ASSIGNABLE_TYPE, classes = ElasticsearchRepository.class))
public class G2WebStoreV2Application {

    public static void main(String[] args) {
        SpringApplication.run(G2WebStoreV2Application.class, args);
    }
}
