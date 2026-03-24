package com.onceClick.recruitmentService.infrastructure.feign;

import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncDataFromAccountHandler {

    private final AuthServiceClient authServiceClient;

    public AuthAccountDto syncAccountData(UUID accountId){

//        // 1. Check and load account data from Redis
//        String cacheKey = "account:" + accountId;
//        AuthAccountDto account = redisTemplate.opsForValue().get(cacheKey);
//
//        // 2. If does't have, load data from authServiceClient
//        if (account == null) {
//            log.info("Syncing account {} from Auth Service", accountId);
//            account = authServiceClient.getAccountById(accountId);
//
//            // Cache 10 minutes
//            redisTemplate.opsForValue().set(cacheKey, account, Duration.ofMinutes(10));
//        }
//        return account;


        try{

            AuthAccountDto account = authServiceClient.getAccountById(accountId);
            return account;

        } catch (Exception e) {
            log.error("Feign call to Auth Service failed! accountId={}, error={}", accountId, e.getMessage(), e);
            throw new ResourceNotFoundException("Account is not sync from Auth Service yet!");
        }
    }
}
