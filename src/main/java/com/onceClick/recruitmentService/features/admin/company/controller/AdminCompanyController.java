package com.onceClick.recruitmentService.features.admin.company.controller;

import com.onceClick.recruitmentService.features.admin.company.dto.AdminCompanyResponseDto;
import com.onceClick.recruitmentService.features.admin.company.handler.AdminGetCompaniesHandler;
import com.onceClick.recruitmentService.features.admin.company.handler.AdminReviewCompanyHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/company")
@RequiredArgsConstructor
@Slf4j
public class AdminCompanyController {

    private final AdminGetCompaniesHandler getCompaniesHandler;
    private final AdminReviewCompanyHandler reviewCompanyHandler;

    // List companies with filters and pagination
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminCompanyResponseDto>>> getAllCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String provinceCode,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String sizeRange,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // Whitelist sortable fields to prevent invalid column injection
        Set<String> allowed = Set.of("createdAt", "companyName", "industry", "status");
        if (!allowed.contains(sortBy)) sortBy = "createdAt";

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(getCompaniesHandler.getCompanies(
                keyword, provinceCode, industry, sizeRange, status, pageable));
    }

    // Approve a pending company
    @PutMapping("/{companyId}/approve")
    public ResponseEntity<ApiResponse<AdminCompanyResponseDto>> approveCompany(
            @PathVariable UUID companyId) {
        return ResponseEntity.ok(reviewCompanyHandler.approve(companyId));
    }

    // Reject a pending company
    @PutMapping("/{companyId}/reject")
    public ResponseEntity<ApiResponse<AdminCompanyResponseDto>> rejectCompany(
            @PathVariable UUID companyId) {
        return ResponseEntity.ok(reviewCompanyHandler.reject(companyId));
    }
}
