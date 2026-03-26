package com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos;

import java.math.BigDecimal;
import java.util.List;

public record ExtractedFields(
        String careerGoal,           // resume.career_goal ⭐⭐
        String major,               // resume.major ⭐⭐
        BigDecimal experienceYear,  // resume.experience_year ⭐⭐
        String salaryExpectation,   // resume.salary_expectation ⭐⭐
        Integer totalExperienceMonths,  // Calculated field
        List<String> topSkills      // Top 5 skills (search)
) {}