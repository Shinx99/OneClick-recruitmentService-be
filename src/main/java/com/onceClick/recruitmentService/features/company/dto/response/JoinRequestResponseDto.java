package com.onceClick.recruitmentService.features.company.dto.response;

import java.time.Instant;
import java.util.UUID;

public record JoinRequestResponseDto(
        UUID id,
        UUID companyId,
        String companyName,
        UUID employerId,
        String employerName,
        String employerEmail,
        String message,
        String status,
        Instant createdAt
) {}
