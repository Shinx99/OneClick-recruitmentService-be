package com.onceClick.recruitmentService.features.employer.controller;


import com.onceClick.recruitmentService.features.employer.dto.request.EmployerRequestDto;
import com.onceClick.recruitmentService.features.employer.dto.response.EmployerResponseDto;
import com.onceClick.recruitmentService.features.employer.handler.EmployerProfileHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/employer")
public class EmployerProfileController {

    private final EmployerProfileHandler employerProfileHandler;
    private final CurrentUser currentUser;


    // Method submitOnboarding() cho recruiter chua duoc onboard
    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<Void>> submitOnboarding(@RequestBody EmployerRequestDto requestDto){

        UUID employerId = currentUser.getCurrentAccountId();
        ApiResponse<Void> response = employerProfileHandler.submitOnboarding(requestDto, employerId);

        return ResponseEntity.ok(response);
    }


    // Method checkOnboardingStatus()
    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @GetMapping("/onboarding-status")
    public ResponseEntity<ApiResponse<String>> checkOnboardingStatus(@RequestBody EmployerRequestDto requestDto){
        UUID employerId = currentUser.getCurrentAccountId();
        String status = employerProfileHandler.checkOnboardingStatus(employerId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }


    // Update profile when recruiter is verified!
    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<EmployerResponseDto>> updateEmployerProfile(@RequestBody EmployerRequestDto employerRequest){

        UUID employerId = currentUser.getCurrentAccountId();
        ApiResponse<EmployerResponseDto> response = employerProfileHandler.updateEmployerProfile(employerRequest, employerId);
        return ResponseEntity.ok(response);
    }

}
