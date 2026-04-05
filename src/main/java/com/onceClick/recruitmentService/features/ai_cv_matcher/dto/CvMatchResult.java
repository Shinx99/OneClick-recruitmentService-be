package com.onceClick.recruitmentService.features.ai_cv_matcher.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// CvMatchResult.java
@Data
@Builder
public class CvMatchResult {
    private String cvText;
    private ParsedCvDto parsedCv;
    private Double matchScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String matchReason;
    private List<String> improvementTips;
}