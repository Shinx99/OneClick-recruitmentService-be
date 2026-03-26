package com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceItem {
    String position;
    String company;
    String duration;
    String description;
}
