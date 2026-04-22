package com.onceClick.recruitmentService.features.employer.handler;

import com.onceClick.recruitmentService.features.employer.dto.request.EmployerRequestDto;
import com.onceClick.recruitmentService.features.employer.dto.response.EmployerResponseDto;
import com.onceClick.recruitmentService.infrastructure.feign.AuthAccountDto;
import com.onceClick.recruitmentService.infrastructure.feign.SyncDataFromAccountHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployerProfileHandler {

    private final EmployerRepository employerRepository;
    private final CompanyRepository companyRepository;
    private final SyncDataFromAccountHandler syncDataFromAccountHandler;



    // HR login the first time with the super recruiter rights and create company
    @Transactional
    public ApiResponse<Void> submitOnboarding(EmployerRequestDto requestDto, UUID employerId){

        // 1. Check is this HR onboarded (stop calling APIS at many times)
        if(employerRepository.existsById(employerId)) {
            throw new IllegalStateException("Employer đã hoàn thành bước gửi thông tin Onboarding!");
        }

        // 2. Sync Account data to Employer
        AuthAccountDto authAccount = syncDataFromAccountHandler.syncAccountData(employerId);

        Employer employer;

        // 3. Decide flow based on company info in request
        boolean hasCompanyPayload = requestDto != null && requestDto.getCompany() != null;
        boolean joinExistingCompany = hasCompanyPayload && requestDto.getCompany().getCompanyId() != null;
        boolean createNewCompany = hasCompanyPayload
                && requestDto.getCompany().getCompanyId() == null
                && requestDto.getCompany().getCompanyName() != null;

        // 3.A. Join an existing company (Script 2)
        if(joinExistingCompany){

            Company existingCompany = companyRepository.findById(requestDto.getCompany().getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công ty trên hệ thống!"));

            employer = Employer.builder()
                    .employerId(employerId)
                    .email(authAccount.getEmail())
                    .phone(authAccount.getPhone())
                    .company(existingCompany)
                    .consentVersion("1.0")
                    .status(authAccount.getStatus())
                    .verifiedAt(null)
                    .createdAt(Instant.now())
                    .isNew(true)
                    .build();
        }

        // 3.B. Super Recruiter create Company together with onboarding (Script 1)
        else if(createNewCompany){
            Company newCompany = Company.builder()
                    .companyId(UUID.randomUUID())
                    .companyName(requestDto.getCompany().getCompanyName())
                    .taxCode(requestDto.getCompany().getTaxCode())
                    .businessLicenseUrl(requestDto.getCompany().getBusinessLicenseUrl())
                    .businessRepName(requestDto.getCompany().getBusinessRepName())
                    .financialProofUrl(requestDto.getCompany().getFinancialProofUrl())
                    .logoUrl(requestDto.getCompany().getLogoUrl())
                    .websiteUrl(requestDto.getCompany().getWebsiteUrl())
                    .provinceCode(requestDto.getCompany().getProvinceCode())
                    .industry(requestDto.getCompany().getIndustry())
                    .sizeRange(requestDto.getCompany().getSizeRange())
                    .overview(requestDto.getCompany().getOverview())
                    .backgroundUrl(requestDto.getCompany().getBackgroundUrl())
                    .address(requestDto.getCompany().getAddress())
                    .createdBy(employerId)
                    .updatedBy(employerId)
                    .verifiedAt(null)
                    .verificationLevel(null)
                    .status("pending")
                    .build();
            companyRepository.save(newCompany);

            employer = Employer.builder()
                    .employerId(employerId)
                    .email(authAccount.getEmail())
                    .phone(authAccount.getPhone())
                    .company(newCompany)
                    .consentVersion("1.0")
                    .status("pending")
                    .verifiedAt(null)
                    .createdAt(Instant.now())
                    .isNew(true)
                    .build();
        }

        // 3.C. Onboarding WITHOUT company (default flow):
        //      employer record is created with company_id = NULL,
        //      user will create company later via POST /api/recruitment/company
        else {
            employer = Employer.builder()
                    .employerId(employerId)
                    .email(authAccount.getEmail())
                    .phone(authAccount.getPhone())
                    .company(null)
                    .consentVersion("1.0")
                    .status("active")   // employer profile is OK; company verification handled later
                    .verifiedAt(null)
                    .createdAt(Instant.now())
                    .isNew(true)
                    .build();
        }

        employerRepository.save(employer);
        log.info("Employer onboarded: employerId={}, hasCompany={}", employerId, employer.getCompany() != null);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Onboarding submitted. pending approval")
                .build();
    }


    // Cập nhật Profile khi và chỉ khi Admin đã duyệt (verifiedAt != null và status == "active")
    @Transactional
    public ApiResponse<EmployerResponseDto> updateEmployerProfile(EmployerRequestDto requestDto, UUID employerId){

        // 1. Check exist employer (Must have)
        Employer employer = employerRepository.findById(employerId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công ty trên hệ thống!"));


        // 2. GUARD CLAUSE: Chặn hoàn toàn nếu chưa được Admin/HR Manager duyệt
        if(employer.getVerifiedAt() == null || !"active".equalsIgnoreCase(employer.getStatus())) {
            throw new IllegalStateException("Tài khoản đang chờ duyệt! Bạn chưa thể cập nhật Profile lúc này!");
        }

        // 3. Check if employer verified with company
        Company company = employer.getCompany();
        if(company == null || company.getVerifiedAt() == null || requestDto.getCompany() == null){
            throw new IllegalStateException("Công ty của bạn chưa được hệ thống xác thực!");
        }

        // 3. Update field from request
        updateNullableFields(employer, requestDto);

        // 4. Save into table Employer of Recruitment DB
        employerRepository.save(employer);
        log.info("Updated employer profile: {}", employerId);

        // 5. return ApiResponse
        return ApiResponse.<EmployerResponseDto>builder()
                .success(true)
                .message("Employer profile updated successfully")
                .data(new EmployerResponseDto(employerId, "Employer profile updated successfully!"))
                .build();
    }


    // Helper
    private void updateNullableFields(Employer employer, EmployerRequestDto request){

        // Update every single fields if request does not null

        // 1. company
        if(request.getCompany() != null) employer.setCompany(request.getCompany());

        // 2. name
        if(request.getName() != null) employer.setName(request.getName());

        // 3. surname
        if(request.getSurname() != null) employer.setSurname(request.getSurname());

        // 4. about
        if(request.getAbout() != null) employer.setAbout(request.getAbout());

        // 5. birthday
        if(request.getBirthday() != null) employer.setBirthday(request.getBirthday());

        // 6. province
        if(request.getProvince() != null) employer.setProvince(request.getProvince());

        // 7. commune
        if(request.getCommune() != null) employer.setCommune(request.getCommune());

        // 8. gender
        if(request.getGender() != null) employer.setGender(request.getGender());

        // 9. industry
        if(request.getIndustry() != null) employer.setIndustry(request.getIndustry());

        // 10. avatar url
        if(request.getAvatarUrl() != null) employer.setAvatarUrl(request.getAvatarUrl());

        // 11. background url
        if(request.getBackgroundUrl() != null) employer.setBackgroundUrl(request.getBackgroundUrl());

        // 12. reference link
        if (request.getReferenceLink() != null) employer.setReferenceLink(request.getReferenceLink());

        // 13. level
        if(request.getLevel() != null) employer.setLevel(request.getLevel());

        // 14. experience year
        if(request.getExperienceYear() != null) employer.setExperienceYear(request.getExperienceYear());

        // 15. cccd
        if(request.getCccd() != null) employer.setCccd(request.getCccd());

        // 16. verification level
        if(request.getVerificationLevel() != null) employer.setVerificationLevel(request.getVerificationLevel());

        // 17. total job posted
        if(request.getTotalJobPosted() != null) employer.setTotalJobPosted(request.getTotalJobPosted());
    }


    // THÊM METHOD NÀY VÀO Handler của bạn
    public String checkOnboardingStatus(UUID employerId) {
        if (employerRepository.existsById(employerId)) {
            return "COMPLETED";
        }
        return "REQUIRE_ONBOARDING";
    }

}
