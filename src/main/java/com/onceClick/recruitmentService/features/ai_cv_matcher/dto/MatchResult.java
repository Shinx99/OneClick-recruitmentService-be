package com.onceClick.recruitmentService.features.ai_cv_matcher.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvData.ParsedCvData;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchResult {
    double matchPercentage;  // 0-100

    @Singular("matchedSkill")
    List<String> matchedSkills;

    @Singular("missingSkill")
    List<String> missingSkills;

    String explanation;  // AI generated 1-2 câu

    ParsedCvData parsedCv;  // Chỉ cho new upload
}
