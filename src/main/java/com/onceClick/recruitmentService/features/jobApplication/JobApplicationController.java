package com.onceClick.recruitmentService.features.jobApplication;

import com.onceClick.recruitmentService.features.jobApplication.dto.request.ApplyJobRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.ApplyJobResponse;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.CandidateApplicationResponse;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.JobApplicationResponse;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
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
    private final CurrentUser currentUser;  // ← Inject CurrentUser

    // 1. Ứng tuyển
    @PostMapping("/jobs/apply")
    public ResponseEntity<ApiResponse<ApplyJobResponse>> apply(
            @RequestBody ApplyJobRequest request) {

        // Lấy userId từ CurrentUser service
        UUID userId = currentUser.getCurrentAccountId();
        log.info("User {} applying for job {}", userId, request.getJobId());

        ApplyJobResponse response = jobApplicationService.apply(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Ứng tuyển thành công", response));
    }

    // 2. Kiểm tra đã ứng tuyển chưa
    @GetMapping("/jobs/{jobId}/check-applied")
    public ResponseEntity<ApiResponse<Boolean>> checkApplied(
            @PathVariable UUID jobId) {

        UUID userId = currentUser.getCurrentAccountId();
        boolean applied = jobApplicationService.hasApplied(jobId, userId);
        return ResponseEntity.ok(ApiResponse.success(applied));
    }

    // 3. Lấy danh sách job đã ứng tuyển (của tôi)
    @GetMapping("/applications/my-applications")
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getMyApplications() {

        UUID userId = currentUser.getCurrentAccountId();
        List<JobApplicationResponse> responses = jobApplicationService.getMyApplications(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 4. Lấy danh sách ứng viên của 1 job (employer)
    @GetMapping("/employer/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<List<CandidateApplicationResponse>>> getJobApplications(
            @PathVariable UUID jobId) {

        // Kiểm tra employer có quyền xem job này không
        UUID employerId = currentUser.getCurrentAccountId();
        log.info("Employer {} viewing applications for job {}", employerId, jobId);

        List<CandidateApplicationResponse> responses = jobApplicationService.getCandidatesByJob(jobId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 5. Cập nhật trạng thái (employer duyệt/từ chối)
    @PatchMapping("/employer/applications/{jobId}/{candidateId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable UUID jobId,
            @PathVariable UUID candidateId,
            @RequestParam String status,
            @RequestParam(required = false) String note) {

        // Kiểm tra employer có quyền update không
        UUID employerId = currentUser.getCurrentAccountId();
        log.info("Employer {} updating status for job {} candidate {}", employerId, jobId, candidateId);

        jobApplicationService.updateStatus(jobId, candidateId, status, note);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", null));
    }

    // 6. Hủy ứng tuyển
    @DeleteMapping("/applications/{jobId}")
    public ResponseEntity<ApiResponse<Void>> cancelApplication(
            @PathVariable UUID jobId) {

        UUID userId = currentUser.getCurrentAccountId();
        log.info("User {} cancelling application for job {}", userId, jobId);

        jobApplicationService.cancelApplication(jobId, userId);
        return ResponseEntity.ok(ApiResponse.success("Hủy ứng tuyển thành công", null));
    }
}