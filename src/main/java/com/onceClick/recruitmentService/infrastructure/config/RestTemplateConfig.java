package com.onceClick.recruitmentService.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {

    RestTemplate rt = new RestTemplate();

     rt.setErrorHandler(new DefaultResponseErrorHandler() {
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            log.error("API ERROR {}: {}", response.getStatusCode(),
                    StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
            super.handleError(response);
        }
    });
    return rt;

    }


}