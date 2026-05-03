package com.onceClick.recruitmentService.features.admin.user.employer.Controller;

import com.onceClick.recruitmentService.features.admin.user.employer.DTO.EmployerDetailResponse;
import com.onceClick.recruitmentService.features.admin.user.employer.DTO.EmployerListResponse;
import com.onceClick.recruitmentService.features.admin.user.employer.Handler.EmployerAdminHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users/employers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_admin')")
public class EmployerAdminController {

    private final EmployerAdminHandler employerAdminHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmployerListResponse>>> getEmployers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<EmployerListResponse> page = employerAdminHandler.getEmployers(status, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/{employerId}")
    public ResponseEntity<ApiResponse<EmployerDetailResponse>> getEmployerDetail(@PathVariable UUID employerId) {
        return ResponseEntity.ok(ApiResponse.success(employerAdminHandler.getEmployerDetail(employerId)));
    }

    @PutMapping("/{employerId}/status")
    public ResponseEntity<ApiResponse<Void>> updateEmployerStatus(@PathVariable UUID employerId, @RequestParam String status) {
        employerAdminHandler.updateEmployerStatus(employerId, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
