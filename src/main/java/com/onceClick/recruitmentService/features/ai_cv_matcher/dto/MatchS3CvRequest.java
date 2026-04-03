package com.onceClick.recruitmentService.features.ai_cv_matcher.dto;

import lombok.Data;

@Data
public class MatchS3CvRequest {
    private String cvS3Url;  // "s3://recruitment-files/candidates/uuid/cv/CV.pdf"
}