package com.onceClick.recruitmentService.features.job.controller;

import com.onceClick.recruitmentService.features.job.dto.request.CreateJobRequestDto;
import com.onceClick.recruitmentService.features.job.dto.response.CreateJobResponseDto;
import com.onceClick.recruitmentService.features.job.dto.response.GetJobsResponseDto;
import com.onceClick.recruitmentService.features.job.handler.CreateJobHandler;
import com.onceClick.recruitmentService.features.job.handler.GetJobsHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/job")
public class JobController {

    private final CreateJobHandler createJobHandler;
    private final GetJobsHandler getJobsHandler;
    private final CurrentUser currentUser;

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateJobResponseDto>> createJob(@RequestBody CreateJobRequestDto request){
        UUID employerId = currentUser.getCurrentAccountId();

        ApiResponse<CreateJobResponseDto> response = createJobHandler.createJobHandler(request, employerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PageResponse<GetJobsResponseDto>>> getAllJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String jobType,
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
                keyword, province, level, jobType, salaryMin, salaryMax, experienceMax, pageable
        );

        return ResponseEntity.ok(response);
    }
}
