package com.onceClick.recruitmentService.features.job.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateJobRequestDto (

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

    String status,

    String imgUrl,

    List<UUID> skillIds,

    List<String> skillNames
)
{}
