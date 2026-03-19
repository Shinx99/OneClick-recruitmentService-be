package com.onceClick.recruitmentService.infrastructure.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternalApiKeyInterceptor implements RequestInterceptor {

    @Value("${internal.api.key}")
    private String internalApiKey;

    @Override
    public void apply(RequestTemplate template){
        template.header("X-Internal-API-Key", internalApiKey);
    }

}
