package com.onceClick.recruitmentService.features.company.controller;

import com.onceClick.recruitmentService.features.company.dto.request.CreateCompanyRequestDto;
import com.onceClick.recruitmentService.features.company.dto.response.*;
import com.onceClick.recruitmentService.features.company.handler.*;
import jakarta.validation.Valid;
import com.onceClick.recruitmentService.features.job.dto.response.GetJobsResponseDto;
import com.onceClick.recruitmentService.features.job.handler.GetJobsHandler;
import com.onceClick.recruitmentService.shared.dto.*;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import com.onceClick.recruitmentService.shared.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/company")
public class CompanyController {

    private final GetCompanyHandler getCompanyHandler;
    private final GetCompaniesHandler getCompaniesHandler;
    private final GetTopCompaniesHandler getTopCompaniesHandler;
    private final GetCompanyFiltersHandler getCompanyFiltersHandler;
    private final UploadCompanyImageHandler uploadCompanyImageHandler;
    private final CreateCompanyHandler createCompanyHandler;
    private final CurrentUser currentUser;
    private final GetJobsHandler  getJobsHandler;

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateCompanyResponseDto>> createCompany(
            @Valid @RequestBody CreateCompanyRequestDto requestDto) {
        UUID employerId = currentUser.getCurrentAccountId();
        return ResponseEntity.ok(createCompanyHandler.createCompany(requestDto, employerId));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PageResponse<GetCompaniesResponseDto>>> getAllCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String provinceCode,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String sizeRange,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Set<String> allowedFields = Set.of("createdAt", "companyName", "industry", "status");
        if (!allowedFields.contains(sortBy)) sortBy = "createdAt";

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // "TP. Hồ Chí Minh" -> "700000"
        String mappedProvinceCode = LocationUtil.getDbCodeFromName(provinceCode);


        return ResponseEntity.ok(getCompaniesHandler.getAllCompanies(keyword, mappedProvinceCode, industry, sizeRange, status, pageable));
    }

    @GetMapping("/top-6")
    public ResponseEntity<ApiResponse<List<TopCompanyResponseDto>>> getTop6Companies() {
        return ResponseEntity.ok(getTopCompaniesHandler.getTop6Companies());
    }

    @GetMapping("/filters")
    public ResponseEntity<ApiResponse<FilterOptionsResponseDto>> getFilters() {
        return ResponseEntity.ok(getCompanyFiltersHandler.getFilters());
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<GetCompanyResponseDto>> getCompanyById(@PathVariable UUID companyId) {
        return ResponseEntity.ok(getCompanyHandler.getCompanyById(companyId));
    }

    @GetMapping("/{companyId}/jobs")
    public ResponseEntity<ApiResponse<PageResponse<GetJobsResponseDto>>> getJobsByCompany(
            @PathVariable UUID companyId,
            Pageable pageable) {

        // Gọi Handler và trả về Response chuẩn
        return ResponseEntity.ok(getJobsHandler.getJobsByCompanyId(companyId, pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PutMapping(value = "/logo/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<GetCompanyResponseDto>> uploadLogo(
            @RequestParam("logoImage") MultipartFile file) throws IOException {
        UUID employerId = currentUser.getCurrentAccountId();
        return ResponseEntity.ok(uploadCompanyImageHandler.uploadLogo(employerId, file));
    }

    @PreAuthorize("hasAuthority('ROLE_recruiter')")
    @PutMapping(value = "/background/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<GetCompanyResponseDto>> uploadBackground(
            @RequestParam("backgroundImage") MultipartFile file) throws IOException {
        UUID employerId = currentUser.getCurrentAccountId();
        return ResponseEntity.ok(uploadCompanyImageHandler.uploadBackground(employerId, file));
    }
}