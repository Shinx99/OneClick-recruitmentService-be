package com.onceClick.recruitmentService.features.admin.company.handler;

import com.onceClick.recruitmentService.features.admin.company.dto.AdminCompanyResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminGetCompaniesHandler {

    private final CompanyRepository companyRepository;

    // Get list of companies with filters and pagination for admin
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<AdminCompanyResponseDto>> getCompanies(
            String keyword, String provinceCode, String industry,
            String sizeRange, String status, Pageable pageable) {

        // Normalize blank strings to null so SQL filter works correctly
        keyword = normalize(keyword);
        provinceCode = normalize(provinceCode);
        industry = normalize(industry);
        sizeRange = normalize(sizeRange);
        status = normalize(status);

        // Reuse existing search query that already supports status filter
        Page<Company> page = companyRepository.searchCompanies(
                keyword, provinceCode, industry, sizeRange, status, pageable);

        Page<AdminCompanyResponseDto> dtoPage = page.map(this::mapToDto);
        return ApiResponse.success("Lấy danh sách công ty thành công",
                PageResponse.from(dtoPage));
    }

    // Map Company entity to admin-facing DTO
    private AdminCompanyResponseDto mapToDto(Company c) {
        return new AdminCompanyResponseDto(
                c.getCompanyId(), c.getCompanyName(), c.getTaxCode(),
                c.getBusinessLicenseUrl(), c.getBusinessRepName(), c.getFinancialProofUrl(),
                c.getLogoUrl(),  c.getBackgroundUrl(),  c.getWebsiteUrl(), c.getProvinceCode(), c.getIndustry(),
                c.getSizeRange(), c.getOverview(), c.getAddress(), c.getCreatedBy(),
                c.getStatus(), c.getVerificationLevel(), c.getVerifiedAt(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }

    // Convert empty/blank string to null for SQL filter
    private String normalize(String v) {
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }
}
