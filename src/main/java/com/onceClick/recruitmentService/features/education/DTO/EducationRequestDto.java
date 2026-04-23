package com.onceClick.recruitmentService.features.education.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationRequestDto {
    private String schoolName;
    private String degree;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent = false;
    private String description;
    private String imageUrl;
    private String referenceLink;
}

