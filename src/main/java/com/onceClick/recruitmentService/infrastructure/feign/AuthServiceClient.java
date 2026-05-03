package com.onceClick.recruitmentService.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "auth-service",
             url = "${auth-service.url}"  ,
             configuration = FeignClientConfig.class
            )

public interface AuthServiceClient {

    @GetMapping("/api/internal/accounts/{accountId}")
    AuthAccountDto getAccountById(@PathVariable("accountId")UUID accountId);

    @PutMapping("/api/internal/accounts/{accountId}/status")
    void updateAccountStatus(@PathVariable("accountId") UUID accountId,
                             @RequestParam("status") String status);
}
