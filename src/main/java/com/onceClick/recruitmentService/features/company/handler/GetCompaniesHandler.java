
package com.onceClick.recruitmentService.features.company.handler;
import com.onceClick.recruitmentService.features.company.dto.response.GetCompaniesResponseDto;
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
public class GetCompaniesHandler {
    private final CompanyRepository companyRepository;
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<GetCompaniesResponseDto>> getAllCompanies(
            String keyword,
            String provinceCode,
            String industry,
            String status,
            Pageable pageable
    ) {
        // Chuẩn hóa input:
        // nếu null, rỗng, hoặc toàn khoảng trắng thì chuyển về null
        keyword = normalize(keyword);
        provinceCode = normalize(provinceCode);
        industry = normalize(industry);
        status = normalize(status);
        log.info("Fetching companies with keyword={}, provinceCode={}, industry={}, status={}",
                keyword, provinceCode, industry, status);
        Page<Company> companyPage = companyRepository.searchCompanies(
                keyword, provinceCode, industry, status, pageable
        );
        Page<GetCompaniesResponseDto> dtoPage = companyPage.map(this::mapToDto);
        PageResponse<GetCompaniesResponseDto> pageResponse = PageResponse.from(dtoPage);
        return ApiResponse.success("Lấy danh sách công ty thành công", pageResponse);
    }
    private GetCompaniesResponseDto mapToDto(Company company) {
        return new GetCompaniesResponseDto(
                company.getCompanyId(),
                company.getCompanyName(),
                company.getLogoUrl(),
                company.getIndustry(),
                company.getProvinceCode(),
                company.getSizeRange(),
                company.getStatus(),
                company.getVerificationLevel(),
                company.getCreatedAt()
        );
    }
    private String normalize(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }
}