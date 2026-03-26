package com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos;

public record EducationInfo(
        String schoolName,
        String degree,
        String fieldOfStudy,
        String startDate,
        String endDate,
        Boolean isCurrent
) {}