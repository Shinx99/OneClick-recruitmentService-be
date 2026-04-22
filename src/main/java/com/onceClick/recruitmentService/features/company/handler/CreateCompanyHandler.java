package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.request.CreateCompanyRequestDto;
import com.onceClick.recruitmentService.features.company.dto.response.CreateCompanyResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
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
public class CreateCompanyHandler {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;

    @Transactional
    public ApiResponse<CreateCompanyResponseDto> createCompany(CreateCompanyRequestDto requestDto, UUID employerId) {

        // 1. Check employer exists (must verify profile first)
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter hasn't verified profile!"));

        // 2. An employer can only own 1 company
        if (employer.getCompany() != null) {
            throw new IllegalStateException("Recruiter already has a company linked!");
        }

        // 3. Uniqueness checks (companyName & taxCode are unique in DB)
        if (companyRepository.existsByCompanyName(requestDto.companyName().trim())) {
            throw new IllegalArgumentException("Company name already exists!");
        }
        if (companyRepository.existsByTaxCode(requestDto.taxCode().trim())) {
            throw new IllegalArgumentException("Tax code already exists!");
        }

        // 4. Build & save company (pending admin verification)
        Company company = Company.builder()
                .companyId(UUID.randomUUID())
                .companyName(requestDto.companyName().trim())
                .taxCode(requestDto.taxCode().trim())
                .businessLicenseUrl(requestDto.businessLicenseUrl())
                .businessRepName(requestDto.businessRepName())
                .financialProofUrl(requestDto.financialProofUrl())
                .logoUrl(requestDto.logoUrl())
                .websiteUrl(requestDto.websiteUrl())
                .provinceCode(requestDto.provinceCode())
                .industry(requestDto.industry())
                .sizeRange(requestDto.sizeRange())
                .overview(requestDto.overview())
                .backgroundUrl(requestDto.backgroundUrl())
                .address(requestDto.address())
                .createdBy(employerId)
                .updatedBy(employerId)
                .status("pending")
                .verifiedAt(null)
                .verificationLevel(null)
                .build();

        Company savedCompany = companyRepository.save(company);

        // 5. Link company into current employer (same transaction)
        employer.setCompany(savedCompany);
        employerRepository.save(employer);

        log.info("Company created: companyId={}, employerId={}", savedCompany.getCompanyId(), employerId);

        // 6. Build response
        CreateCompanyResponseDto response = new CreateCompanyResponseDto(
                savedCompany.getCompanyId(),
                savedCompany.getCompanyName(),
                savedCompany.getTaxCode(),
                savedCompany.getIndustry(),
                savedCompany.getSizeRange(),
                savedCompany.getProvinceCode(),
                savedCompany.getStatus(),
                savedCompany.getVerificationLevel(),
                savedCompany.getVerifiedAt(),
                savedCompany.getCreatedAt()
        );

        return ApiResponse.success("Company created successfully! Awaiting admin verification.", response);
    }
}
