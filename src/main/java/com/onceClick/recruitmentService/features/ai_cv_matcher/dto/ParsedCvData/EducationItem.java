package com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationItem {
    String schoolName;
    String degree;
    String fieldOfStudy;
    String startYear, endYear;
}
