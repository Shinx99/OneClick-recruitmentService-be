package com.onceClick.recruitmentService.features.employer.controller;


import com.onceClick.recruitmentService.features.employer.dto.request.EmployerRequestDto;
import com.onceClick.recruitmentService.features.employer.dto.response.EmployerResponseDto;
import com.onceClick.recruitmentService.features.employer.handler.EmployerProfileHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/employer")
public class EmployerProfileController {

    private final EmployerProfileHandler employerProfileHandler;

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<EmployerResponseDto>> updateEmployerProfile(@RequestBody EmployerRequestDto employerRequest){

        ApiResponse<EmployerResponseDto> response = employerProfileHandler.updateEmployerProfile(employerRequest);
        return ResponseEntity.ok(response);

    }

}
