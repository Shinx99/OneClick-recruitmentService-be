package com.onceClick.recruitmentService.infrastructure.feign;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncDataFromAccountController {

    private final SyncDataFromAccountHandler syncDataFromAccountHandler;

    @GetMapping("/{accountId}")
    public ResponseEntity<AuthAccountDto> syncDataFromAccount(@PathVariable UUID accountId){
        AuthAccountDto accountDto = syncDataFromAccountHandler.syncAccountData(accountId);
        return ResponseEntity.ok(accountDto);
    }

}
