package com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos;

public record WorkExperience(
    String company,
    String position,
    String startDate,
    String endDate,
    String description
) {}