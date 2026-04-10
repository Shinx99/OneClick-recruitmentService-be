package com.onceClick.recruitmentService.features.admin.dashboard.dto;

import java.time.Instant;

public record ActivityItemDTO(
        String id,
        String name,
        Instant created
) {
}
