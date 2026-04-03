package com.onceClick.recruitmentService.features.ai_cv_matcher.dto;

import lombok.Data;

// MatchCachedCvRequest.java
@Data
public class MatchCachedCvRequest {
    private String parsedCvJson;  // JSON string từ Resume.parsed_cv_json
}