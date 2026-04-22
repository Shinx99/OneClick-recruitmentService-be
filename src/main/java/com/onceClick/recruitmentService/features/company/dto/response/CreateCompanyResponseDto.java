package com.onceClick.recruitmentService.features.company.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CreateCompanyResponseDto(

        UUID companyId,
        String companyName,
        String taxCode,
        String industry,
        String sizeRange,
        String provinceCode,
        String status,
        String verificationLevel,
        Instant verifiedAt,
        Instant createdAt

) {}
