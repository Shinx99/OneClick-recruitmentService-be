package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.response.GetCompanyResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMyCompanyHandler {

    private final EmployerRepository employerRepository;

    // Get the company linked to the currently logged-in recruiter
    @Transactional(readOnly = true)
    public ApiResponse<GetCompanyResponseDto> getMyCompany(UUID employerId) {
        log.info("Fetching company for employer {}", employerId);

        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employer", "employerId", employerId));

        Company company = employer.getCompany();
        if (company == null) {
            throw new ResourceNotFoundException(
                    "Bạn chưa thuộc công ty nào!");
        }

        GetCompanyResponseDto dto = new GetCompanyResponseDto(
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

        return ApiResponse.success("Lấy thông tin công ty thành công", dto);
    }
}
