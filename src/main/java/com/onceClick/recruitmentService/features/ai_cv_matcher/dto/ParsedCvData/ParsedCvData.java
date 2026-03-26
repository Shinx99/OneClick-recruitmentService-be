package com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvData;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCvData {
    @Singular("skill")
    List<String> skills;
    
    @Singular("education")
    List<EducationItem> education;
    
    @Singular("experience")
    List<ExperienceItem> experience;
    
    String careerSummary;  // 1-2 câu tóm tắt
}