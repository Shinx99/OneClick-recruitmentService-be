
package com.onceClick.recruitmentService.features.company.dto.response;
import java.time.Instant;
import java.util.UUID;
public record TopCompanyResponseDto(
        UUID companyId,
        String companyName,
        String logoUrl,
        String industry,
        String provinceCode,
        String sizeRange,
        String websiteUrl,
        Instant verifiedAt
) {}