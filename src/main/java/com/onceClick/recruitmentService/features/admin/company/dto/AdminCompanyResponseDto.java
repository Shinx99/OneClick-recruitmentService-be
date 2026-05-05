package com.onceClick.recruitmentService.features.admin.company.dto;

import java.time.Instant;
import java.util.UUID;

public record AdminCompanyResponseDto(
        UUID companyId,
        String companyName,
        String taxCode,
        String businessLicenseUrl,
        String businessRepName,
        String financialProofUrl,
        String logoUrl,
        String backgroundUrl,
        String websiteUrl,
        String provinceCode,
        String industry,
        String sizeRange,
        String overview,
        String address,
        UUID createdBy,
        String status,
        String verificationLevel,
        Instant verifiedAt,
        Instant createdAt,
        Instant updatedAt
) {}
