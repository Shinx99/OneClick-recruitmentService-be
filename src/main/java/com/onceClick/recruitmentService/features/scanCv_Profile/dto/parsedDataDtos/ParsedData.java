package com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos;

import java.util.List;

public record ParsedData(
        PersonalInfo personal,
        List<WorkExperience> workExperience,
        List<EducationInfo> education,
        List<CertificateInfo> certificates,
        List<String> skills,
        ExtractedFields extractedFields  // career_goal, major, exp_year...
) {}