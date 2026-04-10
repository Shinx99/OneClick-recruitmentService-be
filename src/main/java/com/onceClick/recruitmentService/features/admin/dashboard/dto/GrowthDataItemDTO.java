package com.onceClick.recruitmentService.features.admin.dashboard.dto;

public record GrowthDataItemDTO(
        String month,
        long candidates,
        long companies,
        long employers,
        long jobs
) {}
