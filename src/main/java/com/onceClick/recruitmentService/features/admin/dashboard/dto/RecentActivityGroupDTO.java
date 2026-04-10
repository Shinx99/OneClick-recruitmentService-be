package com.onceClick.recruitmentService.features.admin.dashboard.dto;

import java.util.List;

public record RecentActivityGroupDTO(
        String type,
        String title,
        List<ActivityItemDTO> items
) {
}
