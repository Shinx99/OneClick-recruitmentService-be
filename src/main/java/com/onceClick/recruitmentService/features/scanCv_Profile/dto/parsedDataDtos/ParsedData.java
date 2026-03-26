package com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos;


import org.springframework.boot.info.SslInfo;

import java.util.List;

public record ParsedData(
        PersonalInfo personal,
        List<WorkExperience> workExperience,
        List<EducationInfo> education,
        List<SslInfo.CertificateInfo> certificates,
        List<String> skills,
        ExtractedFields extractedFields  // career_goal, major, exp_year...
) {}