package com.onceClick.recruitmentService.features.ai_cv_matcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// CvMatchScore.java (cho match result)
@Data
@AllArgsConstructor
public class CvMatchScore {
    private Double similarity;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String matchReason;
    private List<String> improvementTips;
}