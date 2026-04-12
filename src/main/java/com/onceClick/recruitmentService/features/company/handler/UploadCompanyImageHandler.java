package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.response.GetCompanyResponseDto;
import com.onceClick.recruitmentService.infrastructure.storage.CloudinaryStorageService.CloudinaryStorageService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadCompanyImageHandler {

    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    // -------------------------------------------------------------------------
    // UPLOAD LOGO
    // -------------------------------------------------------------------------
    @Transactional
    public ApiResponse<GetCompanyResponseDto> uploadLogo(UUID employerId, MultipartFile file) throws IOException {

        Company company = getCompanyByEmployerId(employerId);

        // Delete old logo if exists
        String oldLogoUrl = company.getLogoUrl();
        if (oldLogoUrl != null && !oldLogoUrl.isBlank()) {
            String oldPublicId = extractPublicId(oldLogoUrl);
            try {
                cloudinaryStorageService.deleteImage(oldPublicId);
            } catch (IOException e) {
                log.warn("Failed to delete old logo {}: {}", oldPublicId, e.getMessage());
            }
        }

        // Upload new logo
        String logoUrl = cloudinaryStorageService.uploadImage(file, "companies/logos");
        company.setLogoUrl(logoUrl);
        company.setUpdatedBy(employerId);
        company.setUpdatedAt(Instant.now());
        companyRepository.save(company);

        log.info("Updated logo for company: {}", company.getCompanyId());
        return ApiResponse.success("Company logo updated successfully!", mapToDto(company));
    }

    // -------------------------------------------------------------------------
    // UPLOAD BACKGROUND
    // -------------------------------------------------------------------------
    @Transactional
    public ApiResponse<GetCompanyResponseDto> uploadBackground(UUID employerId, MultipartFile file) throws IOException {

        Company company = getCompanyByEmployerId(employerId);

        // Delete old background if exists
        String oldBackgroundUrl = company.getBackgroundUrl();
        if (oldBackgroundUrl != null && !oldBackgroundUrl.isBlank()) {
            String oldPublicId = extractPublicId(oldBackgroundUrl);
            try {
                cloudinaryStorageService.deleteImage(oldPublicId);
            } catch (IOException e) {
                log.warn("Failed to delete old background {}: {}", oldPublicId, e.getMessage());
            }
        }

        // Upload new background
        String backgroundUrl = cloudinaryStorageService.uploadImage(file, "companies/backgrounds");
        company.setBackgroundUrl(backgroundUrl);
        company.setUpdatedBy(employerId);
        company.setUpdatedAt(Instant.now());
        companyRepository.save(company);

        log.info("Updated background for company: {}", company.getCompanyId());
        return ApiResponse.success("Company background updated successfully!", mapToDto(company));
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------
    private Company getCompanyByEmployerId(UUID employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));
        return employer.getCompany();
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                log.warn("Unexpected Cloudinary URL format: {}", imageUrl);
                return null;
            }
            String afterUpload = parts[1];
            String withoutVersion = afterUpload.replaceAll("^v\\d+/", "");
            return withoutVersion.replaceAll("\\.[a-zA-Z0-9]+$", "");
        } catch (Exception e) {
            log.warn("Cannot extract publicId from URL: {}", imageUrl);
            return null;
        }
    }

    private GetCompanyResponseDto mapToDto(Company c) {
        return new GetCompanyResponseDto(
                c.getCompanyId(), c.getCompanyName(), c.getTaxCode(),
                c.getBusinessLicenseUrl(), c.getBusinessRepName(), c.getFinancialProofUrl(),
                c.getLogoUrl(), c.getWebsiteUrl(), c.getProvinceCode(),
                c.getIndustry(), c.getSizeRange(), c.getOverview(),
                c.getBackgroundUrl(), c.getAddress(), c.getCreatedBy(), c.getUpdatedBy(),
                c.getVerifiedAt(), c.getVerificationLevel(), c.getStatus(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}
