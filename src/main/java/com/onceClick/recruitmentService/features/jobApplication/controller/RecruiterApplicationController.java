package com.onceClick.recruitmentService.features.jobApplication.controller;

import com.onceClick.recruitmentService.features.jobApplication.dto.request.ApplicationFilterRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.request.InterviewScheduleRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.request.UpdateStatusRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.*;
import com.onceClick.recruitmentService.features.jobApplication.service.RecruiterApplicationService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
public class RecruiterApplicationController {

    private final RecruiterApplicationService recruiterApplicationService;
    private final CurrentUser currentUser;

    /**
     * Lấy danh sách jobs của employer hiện tại
     * GET /api/employer/jobs
     */
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<EmployerJobResponse>>> getMyJobs() {
        UUID employerId = currentUser.getCurrentAccountId();
        log.info("Employer {} getting their jobs", employerId);

        List<EmployerJobResponse> jobs = recruiterApplicationService.getMyJobs(employerId);
        return ResponseEntity.ok(ApiResponse.success(jobs));
    }

    /**
     * Lấy chi tiết job của employer
     * GET /api/employer/jobs/{jobId}
     */
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<ApiResponse<EmployerJobResponse>> getJobDetail(@PathVariable UUID jobId) {
        UUID employerId = currentUser.getCurrentAccountId();
        log.info("Employer {} getting job detail for job {}", employerId, jobId);

        EmployerJobResponse job = recruiterApplicationService.getJobDetail(jobId, employerId);
        return ResponseEntity.ok(ApiResponse.success(job));
    }

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<Page<ApplicationListResponse>>> getApplications(
            @PathVariable UUID jobId,
            @ModelAttribute ApplicationFilterRequest filter) {
        UUID employerId = currentUser.getCurrentAccountId();
        log.info("Employer {} getting applications for job {}", employerId, jobId);
        Page<ApplicationListResponse> applications = recruiterApplicationService.getApplications(jobId, filter, employerId);
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @GetMapping("/jobs/{jobId}/applications/stats")
    public ResponseEntity<ApiResponse<ApplicationStatsResponse>> getApplicationStats(@PathVariable UUID jobId) {
        UUID employerId = currentUser.getCurrentAccountId();
        ApplicationStatsResponse stats = recruiterApplicationService.getApplicationStats(jobId, employerId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<ApplicationDetailResponse>> getApplicationDetail(@PathVariable UUID applicationId) {
        UUID employerId = currentUser.getCurrentAccountId();
        ApplicationDetailResponse detail = recruiterApplicationService.getApplicationDetail(applicationId, employerId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @PatchMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable UUID applicationId,
            @Valid @RequestBody UpdateStatusRequest request) {
        UUID employerId = currentUser.getCurrentAccountId();
        log.info("Employer {} updating status for application {} to {}", employerId, applicationId, request.getStatus());
        recruiterApplicationService.updateStatus(applicationId, request, employerId);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", null));
    }

    @GetMapping("/applications/{applicationId}/history")
    public ResponseEntity<ApiResponse<List<StatusHistoryResponse>>> getApplicationHistory(@PathVariable UUID applicationId) {
        UUID employerId = currentUser.getCurrentAccountId();
        List<StatusHistoryResponse> history = recruiterApplicationService.getApplicationHistory(applicationId, employerId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @PostMapping("/applications/{applicationId}/schedule-interview")
    public ResponseEntity<ApiResponse<InterviewScheduleResponse>> scheduleInterview(
            @PathVariable UUID applicationId,
            @Valid @RequestBody InterviewScheduleRequest request) {
        UUID employerId = currentUser.getCurrentAccountId();
        InterviewScheduleResponse response = recruiterApplicationService.scheduleInterview(applicationId, request, employerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/schedule-interview/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> cancelInterview(
            @PathVariable UUID scheduleId,
            @RequestParam(required = false) String reason) {
        UUID employerId = currentUser.getCurrentAccountId();
        recruiterApplicationService.cancelInterview(scheduleId, reason, employerId);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy lịch phỏng vấn", null));
    }
}