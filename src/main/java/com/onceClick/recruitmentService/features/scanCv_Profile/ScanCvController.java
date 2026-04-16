package com.onceClick.recruitmentService.features.scanCv_Profile;

import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ParsedResumeResponse;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ScanCvRequest;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class  ScanCvController {

    private final ScanCvHandler scanCvHandler;
    private final CurrentUser currentUser;

    @PostMapping("/scan-cv")
    public ApiResponse<ParsedResumeResponse> scanCv(
            @Valid @ModelAttribute ScanCvRequest request) {

        UUID accountId = currentUser.getCurrentAccountId();
        log.info("Account ID: {}", accountId);

        ParsedResumeResponse result = scanCvHandler.handle(accountId,request);
        return ApiResponse.success(result);
    }
}





 /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            log.info("🔍 All claims: {}", jwt.getClaims());  // In tất cả claims
            String accountId = jwt.getClaimAsString("accountId");
            if (accountId == null) {
                String sub = jwt.getClaimAsString("sub");  // Thường là user ID
                log.warn("accountId null, dùng sub: {}", sub);
                accountId = sub;  // Fallback
            }
            log.info("🆔 Account ID: {}", accountId);
        }*/
