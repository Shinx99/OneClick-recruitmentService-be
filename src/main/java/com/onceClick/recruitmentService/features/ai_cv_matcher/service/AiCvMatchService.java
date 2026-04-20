package com.onceClick.recruitmentService.features.ai_cv_matcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.CvMatchResult;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.CvMatchScore;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvDto;
import com.onceClick.recruitmentService.infrastructure.ai.DeepSeekService;
import com.onceClick.recruitmentService.infrastructure.ai.prompt.AiPrompts;
import com.onceClick.recruitmentService.infrastructure.processor.S3FileExtractorService;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AiCvMatchService {

    private final S3FileExtractorService s3Extractor;
    private final DeepSeekService deepSeekService;
    private final ObjectMapper objectMapper;
    private final AiPrompts aiPrompts;

    /**
     * FULL FLOW: S3 → Extract → Scan → Match (3.5s)
     */
    public CvMatchResult scanAndMatchCv(String cvS3Url, Job job) throws Exception {
        String cvText = s3Extractor.extractTextFromS3(cvS3Url);

        if (cvText.startsWith("[IMAGE_SCAN_CV]")) {
            return CvMatchResult.builder()
                    .cvText(cvText)
                    .matchScore(0.0)
                    .matchReason("CV là ảnh scan, cần OCR")
                    .build();
        }

        // Scan CV
        String scanPrompt = aiPrompts.getSystemPromptForScanCV("scan_cv");
        String parsedJson = deepSeekService.chat("scan_cv", scanPrompt, cvText);
        ParsedCvDto parsedCv = objectMapper.readValue(parsedJson, ParsedCvDto.class);

        // Match
        return matchWithParsedCv(parsedCv, job);
    }

    /**
     * FAST MATCH: Cache hit → chỉ match (1-2s)
     */
    public CvMatchResult matchWithCachedCv(ParsedCvDto cachedCv, Job job) throws Exception {
        log.info("CACHE HIT: Fast match only");

        String cvJson = objectMapper.writeValueAsString(cachedCv);
        String jobPrompt = aiPrompts.buildJobPrompt(job);
        String matchPrompt = aiPrompts.buildCvJobMatchPrompt(cvJson, jobPrompt);

        String matchJson = deepSeekService.chat("ai_matching", "", matchPrompt);

        // Log response trước khi parse
        log.info("Response from AI (first 500 chars): {}",
                matchJson.length() > 500 ? matchJson.substring(0, 500) : matchJson);

        CvMatchScore score = objectMapper.readValue(matchJson, CvMatchScore.class);

        return CvMatchResult.builder()
                .parsedCv(cachedCv)
                .matchScore(score.getSimilarity())
                .matchedSkills(score.getMatchedSkills() != null ? score.getMatchedSkills() : List.of())
                .missingSkills(score.getMissingSkills() != null ? score.getMissingSkills() : List.of())
                .matchReason(score.getMatchReason())
                .improvementTips(score.getImprovementTips() != null ? score.getImprovementTips() : List.of())
                .build();
    }

    /**
     * INTERNAL: Match với ParsedCv đã có (dùng cho cả cache/fresh)
     */
    private CvMatchResult matchWithParsedCv(ParsedCvDto parsedCv, Job job) throws Exception {
        String cvJson = objectMapper.writeValueAsString(parsedCv);
        String jobPrompt = aiPrompts.buildJobPrompt(job);
        String matchPrompt = aiPrompts.buildCvJobMatchPrompt(cvJson, jobPrompt);

        String matchJson = deepSeekService.chat("ai_matching", "", matchPrompt);
        CvMatchScore score = objectMapper.readValue(matchJson, CvMatchScore.class);

        return CvMatchResult.builder()
                .parsedCv(parsedCv)
                .matchScore(score.getSimilarity())
                .matchedSkills(score.getMatchedSkills())
                .missingSkills(score.getMissingSkills())
                .matchReason(score.getMatchReason())
                .improvementTips(score.getImprovementTips())
                .build();
    }

    /**
     * Bonus: Match với raw text (cho luồng new CV direct)
     */
    public CvMatchResult scanAndMatchText(String cvText, Job job) throws Exception {
        // 1. Scan
        String scanPrompt = aiPrompts.getSystemPromptForScanCV("scan_cv");
        String parsedJson = deepSeekService.chat("scan_cv", scanPrompt, cvText);
        ParsedCvDto parsedCv = objectMapper.readValue(parsedJson, ParsedCvDto.class);

        // 2. Match
        return matchWithParsedCv(parsedCv, job);
    }

}