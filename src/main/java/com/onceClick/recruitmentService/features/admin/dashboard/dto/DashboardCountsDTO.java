package com.onceClick.recruitmentService.features.admin.dashboard.dto;

public record DashboardCountsDTO(
        long totalCandidates,
        long totalCompanies,
        long totalEmployers,
        long totalJobs
) {}
