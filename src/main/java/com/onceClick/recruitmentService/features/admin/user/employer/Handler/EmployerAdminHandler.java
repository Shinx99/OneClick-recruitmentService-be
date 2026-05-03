package com.onceClick.recruitmentService.features.admin.user.employer.Handler;

import com.onceClick.recruitmentService.features.admin.user.employer.DTO.EmployerDetailResponse;
import com.onceClick.recruitmentService.features.admin.user.employer.DTO.EmployerListResponse;
import com.onceClick.recruitmentService.infrastructure.feign.AuthServiceClient;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployerAdminHandler {
    private final EmployerRepository employerRepository;
    private final AuthServiceClient authServiceClient;

    @Transactional(readOnly = true)
    public Page<EmployerListResponse> getEmployers(String status, String keyword, Pageable pageable) {
        String keywordParam = null;
        if (keyword != null && !keyword.isBlank()) {
            keywordParam = "%" + keyword.trim().toLowerCase() + "%";
        }
        return employerRepository
                .findFilteredEmployers(status, keywordParam, pageable)
                .map(this::toListResponse);
    }

    @Transactional(readOnly = true)
    public EmployerDetailResponse getEmployerDetail(UUID employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        return toDetailResponse(employer);
    }

    @Transactional
    public void updateEmployerStatus(UUID employerId, String newStatus) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        employer.setStatus(newStatus);
        employerRepository.save(employer);

        authServiceClient.updateAccountStatus(employerId, newStatus);
    }

    private EmployerListResponse toListResponse(Employer e) {
        return EmployerListResponse.builder()
                .employerId(e.getEmployerId())
                .surname(e.getSurname())
                .name(e.getName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .companyName(e.getCompany() != null ? e.getCompany().getCompanyName() : null)
                .status(e.getStatus())
                .build();
    }

    private EmployerDetailResponse toDetailResponse(Employer e) {
        return EmployerDetailResponse.builder()
                .employerId(e.getEmployerId())
                .email(e.getEmail())
                .phone(e.getPhone())
                .surname(e.getSurname())
                .name(e.getName())
                .birthday(e.getBirthday())
                .province(e.getProvince())
                .commune(e.getCommune())
                .gender(e.getGender())
                .about(e.getAbout())
                .avatarUrl(e.getAvatarUrl())
                .backgroundUrl(e.getBackgroundUrl())
                .referenceLink(e.getReferenceLink())
                .cccd(e.getCccd())
                .cccdVerifiedAt(e.getCccdVerifiedAt())
                .verificationLevel(e.getVerificationLevel())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .companyId(e.getCompany() != null ? e.getCompany().getCompanyId() : null)
                .companyName(e.getCompany() != null ? e.getCompany().getCompanyName() : null)
                .companyTaxCode(e.getCompany() != null ? e.getCompany().getTaxCode() : null)
                .build();
    }
}
