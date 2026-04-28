package com.onceClick.recruitmentService.features.job.controller;

import com.onceClick.recruitmentService.features.job.dto.request.CreateJobRequestDto;
import com.onceClick.recruitmentService.features.job.dto.request.UpdateJobRequestDto;
import com.onceClick.recruitmentService.features.job.dto.response.CreateJobResponseDto;

import com.onceClick.recruitmentService.features.job.dto.response.GetJobDetailResponseDto;
import com.onceClick.recruitmentService.features.job.dto.response.GetJobsResponseDto;
import com.onceClick.recruitmentService.features.job.handler.CreateJobHandler;
import com.onceClick.recruitmentService.features.job.handler.DeleteJobHandler;
import com.onceClick.recruitmentService.features.job.handler.GetJobDetailHandler;
import com.onceClick.recruitmentService.features.job.handler.GetJobsHandler;
import com.onceClick.recruitmentService.features.job.handler.GetRelatedJobsHandler;
import com.onceClick.recruitmentService.features.job.handler.UpdateJobHandler;
import com.onceClick.recruitmentService.features.job.handler.UploadJobImageHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/job")
public class JobController {

    private final CreateJobHandler createJobHandler;
    private final UpdateJobHandler updateJobHandler;
    private final DeleteJobHandler deleteJobHandler;
    private final UploadJobImageHandler uploadJobImageHandler;
    private final GetJobsHandler getJobsHandler;
    private final GetJobDetailHandler getJobDetailHandler;
    private final GetRelatedJobsHandler getRelatedJobsHandler;
    private final CurrentUser currentUser;

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateJobResponseDto>> createJob(@RequestBody CreateJobRequestDto request){
        UUID employerId = currentUser.getCurrentAccountId();

        ApiResponse<CreateJobResponseDto> response = createJobHandler.createJobHandler(request, employerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<GetJobDetailResponseDto>> getJobDetail(@PathVariable UUID jobId,
                                                                             HttpServletRequest request) {
        return ResponseEntity.ok(getJobDetailHandler.getJobDetail(jobId, request));
    }

    @GetMapping("/{jobId}/related")
    public ResponseEntity<ApiResponse<List<GetJobsResponseDto>>> getRelatedJobs(
            @PathVariable UUID jobId
    ) {
        return ResponseEntity.ok(getRelatedJobsHandler.getRelatedJobs(jobId));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PageResponse<GetJobsResponseDto>>> getAllJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal salaryMin,
            @RequestParam(required = false) BigDecimal salaryMax,
            @RequestParam(required = false) BigDecimal experienceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        // Xử lý hướng sắp xếp (tăng dần/giảm dần)
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Tạo đối tượng phân trang
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Gọi Handler
        ApiResponse<PageResponse<GetJobsResponseDto>> response = getJobsHandler.getAllJobs(
                keyword, province, level, jobType, status, salaryMin, salaryMax, experienceMax, pageable
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<CreateJobResponseDto>> updateJob(
            @PathVariable UUID jobId,
            @RequestBody UpdateJobRequestDto request
    ) {
        UUID employerId = currentUser.getCurrentAccountId();
        ApiResponse<CreateJobResponseDto> response = updateJobHandler.updateJob(jobId, request, employerId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable UUID jobId) {
        UUID employerId = currentUser.getCurrentAccountId();
        ApiResponse<Void> response = deleteJobHandler.deleteJob(jobId, employerId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PutMapping(value = "/{jobId}/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateJobResponseDto>> uploadJobImage(
            @PathVariable UUID jobId,
            @RequestParam("jobImage") MultipartFile file
    ) throws IOException {
        UUID employerId = currentUser.getCurrentAccountId();
        return ResponseEntity.ok(uploadJobImageHandler.uploadJobImage(jobId, employerId, file));
    }
}
