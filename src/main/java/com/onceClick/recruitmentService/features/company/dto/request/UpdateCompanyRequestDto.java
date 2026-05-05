package com.onceClick.recruitmentService.features.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request DTO for recruiter updating their own company info
// Note: status, verifiedAt, verificationLevel are NOT updatable here (admin-only)
// Note: logoUrl, backgroundUrl are updated via separate upload endpoints
public record UpdateCompanyRequestDto(

        @NotBlank(message = "Company name is required")
        String companyName,

        @NotBlank(message = "Tax code is required")
        @Size(max = 20, message = "Tax code must be at most 20 characters")
        String taxCode,

        String businessLicenseUrl,

        String businessRepName,

        String financialProofUrl,

        String websiteUrl,

        String provinceCode,

        String industry,

        @NotBlank(message = "Size range is required")
        String sizeRange,

        @NotBlank(message = "Overview is required")
        String overview,

        String address

) {}
