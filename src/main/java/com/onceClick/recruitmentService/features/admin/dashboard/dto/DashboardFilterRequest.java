package com.onceClick.recruitmentService.features.admin.dashboard.dto;

import java.time.YearMonth;

public record DashboardFilterRequest(
        YearMonth month
) {}
