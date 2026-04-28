package com.onceClick.recruitmentService.features.jobApplication.controller;

import com.onceClick.recruitmentService.features.jobApplication.dto.request.ApplyJobRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.ApplyJobResponse;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.JobApplicationResponse;
import com.onceClick.recruitmentService.features.jobApplication.service.JobApplicationService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final CurrentUser currentUser;

    @PostMapping("/jobs/apply")
    public ResponseEntity<ApiResponse<ApplyJobResponse>> apply(@Valid @RequestBody ApplyJobRequest request) {
        UUID userId = currentUser.getCurrentAccountId();
        log.info("User {} applying for job {}", userId, request.getJobId());
        ApplyJobResponse response = jobApplicationService.apply(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Ứng tuyển thành công", response));
    }

    @GetMapping("/jobs/{jobId}/check-applied")
    public ResponseEntity<ApiResponse<Boolean>> checkApplied(@PathVariable UUID jobId) {
        UUID userId = currentUser.getCurrentAccountId();
        boolean applied = jobApplicationService.hasApplied(jobId, userId);
        return ResponseEntity.ok(ApiResponse.success(applied));
    }

    @GetMapping("/applications/my-applications")
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getMyApplications() {
        UUID userId = currentUser.getCurrentAccountId();
        List<JobApplicationResponse> responses = jobApplicationService.getMyApplications(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @DeleteMapping("/applications/{jobId}")
    public ResponseEntity<ApiResponse<Void>> cancelApplication(@PathVariable UUID jobId) {
        UUID userId = currentUser.getCurrentAccountId();
        log.info("User {} cancelling application for job {}", userId, jobId);
        jobApplicationService.cancelApplication(jobId, userId);
        return ResponseEntity.ok(ApiResponse.success("Hủy ứng tuyển thành công", null));
    }
}