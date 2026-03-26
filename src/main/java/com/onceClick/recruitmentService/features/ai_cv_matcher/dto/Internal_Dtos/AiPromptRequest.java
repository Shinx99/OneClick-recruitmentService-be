package com.onceClick.recruitmentService.features.ai_cv_matcher.dto.Internal_Dtos;

import lombok.Data;

import java.util.Map;

@Data
public class AiPromptRequest {
    String task;  // "scan_cv", "match_explain"
    String content;
    Map<String, Object> context;
}