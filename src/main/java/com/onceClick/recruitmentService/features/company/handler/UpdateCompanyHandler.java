package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.request.UpdateCompanyRequestDto;
import com.onceClick.recruitmentService.features.company.dto.response.GetCompanyResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ForbiddenException;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateCompanyHandler {

    private final EmployerRepository employerRepository;
    private final CompanyRepository companyRepository;

    // Update the company info of the currently logged-in recruiter
    @Transactional
    public ApiResponse<GetCompanyResponseDto> updateMyCompany(
            UUID employerId, UpdateCompanyRequestDto req) {
        log.info("Employer {} updating own company", employerId);

        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employer", "employerId", employerId));

        Company company = employer.getCompany();
        if (company == null) {
            throw new ResourceNotFoundException(
                    "Bạn chưa thuộc công ty nào!");
        }

        // Only the company creator can edit the company info
        if (company.getCreatedBy() == null || !company.getCreatedBy().equals(employerId)) {
            throw new ForbiddenException("company", "update");
        }

        String newName = req.companyName().trim();
        String newTaxCode = req.taxCode().trim();

        // Check duplicate company name (skip if unchanged)
        if (!newName.equals(company.getCompanyName())
                && companyRepository.existsByCompanyName(newName)) {
            throw new IllegalArgumentException("Tên công ty đã tồn tại!");
        }

        // Check duplicate tax code (skip if unchanged)
        if (!newTaxCode.equals(company.getTaxCode())
                && companyRepository.existsByTaxCode(newTaxCode)) {
            throw new IllegalArgumentException("Mã số thuế đã tồn tại!");
        }

        // Apply updates — logo/background are updated via separate upload endpoints
        company.setCompanyName(newName);
        company.setTaxCode(newTaxCode);
        company.setBusinessLicenseUrl(req.businessLicenseUrl());
        company.setBusinessRepName(req.businessRepName());
        company.setFinancialProofUrl(req.financialProofUrl());
        company.setWebsiteUrl(req.websiteUrl());
        company.setProvinceCode(req.provinceCode());
        company.setIndustry(req.industry());
        company.setSizeRange(req.sizeRange());
        company.setOverview(req.overview());
        company.setAddress(req.address());
        company.setUpdatedBy(employerId);

        Company saved = companyRepository.save(company);

        GetCompanyResponseDto dto = new GetCompanyResponseDto(
                saved.getCompanyId(),
                saved.getCompanyName(),
                saved.getTaxCode(),
                saved.getBusinessLicenseUrl(),
                saved.getBusinessRepName(),
                saved.getFinancialProofUrl(),
                saved.getLogoUrl(),
                saved.getWebsiteUrl(),
                saved.getProvinceCode(),
                saved.getIndustry(),
                saved.getSizeRange(),
                saved.getOverview(),
                saved.getBackgroundUrl(),
                saved.getAddress(),
                saved.getCreatedBy(),
                saved.getUpdatedBy(),
                saved.getVerifiedAt(),
                saved.getVerificationLevel(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );

        return ApiResponse.success("Cập nhật thông tin công ty thành công!", dto);
    }
}
