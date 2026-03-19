package com.onceClick.recruitmentService.infrastructure.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.Logger;


@Configuration
public class FeignClientConfig {

    @Bean
    public InternalApiKeyInterceptor internalApiKeyInterceptor(){
        return new InternalApiKeyInterceptor();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

}
