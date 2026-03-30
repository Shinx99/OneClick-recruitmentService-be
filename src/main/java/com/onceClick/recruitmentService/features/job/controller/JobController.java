package com.onceClick.recruitmentService.features.job.controller;

import com.onceClick.recruitmentService.features.job.dto.request.CreateJobRequestDto;
import com.onceClick.recruitmentService.features.job.dto.response.CreateJobResponseDto;
import com.onceClick.recruitmentService.features.job.handler.CreateJobHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/job")
public class JobController {

    private final CreateJobHandler createJobHandler;
    private final CurrentUser currentUser;

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateJobResponseDto>> createJob(@RequestBody CreateJobRequestDto request){
        UUID employerId = currentUser.getCurrentAccountId();

        ApiResponse<CreateJobResponseDto> response = createJobHandler.createJobHandler(request, employerId);
        return ResponseEntity.ok(response);
    }
}
