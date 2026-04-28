package com.onceClick.recruitmentService.features.jobApplication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvDto;
import com.onceClick.recruitmentService.features.ai_cv_matcher.service.AiCvMatchService;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchScoreCalculator {

    private final AiCvMatchService aiCvMatchService;
    private final ObjectMapper objectMapper;

    /**
     * Tính match score từ resume và job
     * @return điểm match (0-100, có thể là số thập phân) hoặc null
     */
    public BigDecimal calculate(Resume resume, Job job) {
        if (resume == null || job == null) {
            log.warn("Cannot calculate match score: resume or job is null");
            return null;
        }

        if (resume.getParsedData() == null) {
            log.warn("Resume {} has no parsed data", resume.getResumeId());
            return null;
        }

        try {
            ParsedCvDto parsedCv = objectMapper.convertValue(resume.getParsedData(), ParsedCvDto.class);

            // AiCvMatchService trả về Double (ví dụ: 85.5)
            Double matchScoreDouble = aiCvMatchService.calculateMatchScoreOnly(parsedCv, job);

            if (matchScoreDouble == null) {
                return null;
            }

            // Chuyển Double -> BigDecimal, làm tròn 2 số thập phân
            BigDecimal matchScore = BigDecimal.valueOf(matchScoreDouble)
                    .setScale(2, RoundingMode.HALF_UP);

            log.info("Calculated match score for resume {}: {}", resume.getResumeId(), matchScore);
            return matchScore;

        } catch (Exception e) {
            log.error("Failed to calculate match score for resume {}: {}", resume.getResumeId(), e.getMessage());
            return null;
        }
    }
}