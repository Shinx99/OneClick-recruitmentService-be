package com.onceClick.recruitmentService.features.job.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GetJobDetailResponseDto(
        UUID jobId,
        UUID companyId,
        String companyName,
        String companyLogoUrl,
        String title,
        String description,
        String requirement,
        String majorPreferred,
        String level,
        String jobType,
        String province,
        String commune,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        BigDecimal experienceMinYear,
        LocalDate applicationDeadline,
        Integer applicationCount,
        Integer viewCount,
        String status,
        String imgUrl,
        List<SkillInfo> skills,
        UUID createdBy,
        UUID updatedBy,
        Instant createdAt,
        Instant updatedAt
) {
    public record SkillInfo(
            UUID skillId,
            String skillName
    ) {}
}
