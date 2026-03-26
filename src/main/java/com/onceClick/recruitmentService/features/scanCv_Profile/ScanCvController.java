package com.onceClick.recruitmentService.features.scanCv_Profile;

import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ParsedResumeResponse;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ScanCvRequest;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ScanCvController {

    private final ScanCvHandler scanCvHandler;

    @PostMapping("/scan-cv")
    public ApiResponse<ParsedResumeResponse> scanCv(
            @Valid @ModelAttribute ScanCvRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }

        // getName() = jwt.sub = accountId UUID
        UUID accountId = UUID.fromString(auth.getName());
        log.info("🆔 Account ID: {}", accountId);

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
