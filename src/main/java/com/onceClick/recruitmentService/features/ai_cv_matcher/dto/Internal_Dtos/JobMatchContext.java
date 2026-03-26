package com.onceClick.recruitmentService.features.ai_cv_matcher.dto.Internal_Dtos;

import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvData.ParsedCvData;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JobMatchContext {
    Job job;
    ParsedCvData cv;
    double similarityScore;
    List<String> jobSkills;
}
