package com.onceClick.recruitmentService.features.company.handler;
import com.onceClick.recruitmentService.features.company.dto.response.GetCompanyResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class GetCompanyHandler {
    private final CompanyRepository companyRepository;
    @Transactional(readOnly = true)
    public ApiResponse<GetCompanyResponseDto> getCompanyById(UUID companyId) {
        // Tìm company theo id
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công ty"));
        // Map entity sang response DTO
        GetCompanyResponseDto responseDto = new GetCompanyResponseDto(
                company.getCompanyId(),
                company.getCompanyName(),
                company.getTaxCode(),
                company.getBusinessLicenseUrl(),
                company.getBusinessRepName(),
                company.getFinancialProofUrl(),
                company.getLogoUrl(),
                company.getWebsiteUrl(),
                company.getProvinceCode(),
                company.getIndustry(),
                company.getSizeRange(),
                company.getOverview(),
                company.getBackgroundUrl(),
                company.getAddress(),
                company.getCreatedBy(),
                company.getUpdatedBy(),
                company.getVerifiedAt(),
                company.getVerificationLevel(),
                company.getStatus(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
        return ApiResponse.success("Lấy chi tiết công ty thành công", responseDto);
    }
}