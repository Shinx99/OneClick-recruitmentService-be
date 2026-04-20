package com.onceClick.recruitmentService.features.ai_cv_matcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.features.ai_cv_matcher.handler.AiCvMatchHandler;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.CvMatchResult;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.MatchCachedCvRequest;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.MatchS3CvRequest;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvDto;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/ai-cv-match")
@RequiredArgsConstructor
public class AiCvMatchController {

    private final AiCvMatchHandler handler;
    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;

    /**
     * ENDPOINT 1: Existing S3 URL → Full flow (3.5s)
     * Dùng cho: External CV, legacy S3 links
     */
    @PostMapping("/s3/{jobId}")
    public ResponseEntity<CvMatchResult> matchS3Cv(
            @PathVariable UUID jobId,
            @RequestBody MatchS3CvRequest request) throws Exception {

        log.info("API /s3/{} - CV: {}", jobId, request.getCvS3Url());
        Job job = getJobOrThrow(jobId);
        val result = handler.handleExistingCv(request.getCvS3Url(), job);
        return ResponseEntity.ok(result);
    }

    /**
     * ENDPOINT 2: Cached ParsedCv JSON → Fast match (1s) ⭐
     * Dùng cho: Resume cache hit
     */
    @PostMapping("/resume/{resumeId}/job/{jobId}")
    public ResponseEntity<CvMatchResult> matchResumeCv(
            @PathVariable UUID resumeId,
            @PathVariable UUID jobId) throws Exception {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        // Handler chỉ điều phối, không chứa logic AI/cache ở đây
        CvMatchResult result = handler.handleResumeCv(resumeId, job);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/cached/{jobId}")
    public ResponseEntity<CvMatchResult> matchCachedCv(
            @PathVariable UUID jobId,
            @RequestBody MatchCachedCvRequest request) throws Exception {

        log.info("API /cached/{} - Resume cache", jobId);
        Job job = getJobOrThrow(jobId);
        val parsedCv = objectMapper.readValue(request.getParsedCvJson(), ParsedCvDto.class);
        val result = handler.handleCachedCv(parsedCv, job);
        return ResponseEntity.ok(result);
    }

    /**
     * ENDPOINT 3: New file upload → Direct extract (3.3s)
     * Dùng cho: Test CV mới chưa lưu resume
     */
    @PostMapping(value = "/new/{jobId}", consumes = "multipart/form-data")
    public ResponseEntity<CvMatchResult> matchNewFileCv(
            @PathVariable UUID jobId,
            @RequestParam("file") MultipartFile cvFile) throws Exception {

        log.info("API /new/{} - File: {} ({})",
                jobId, cvFile.getOriginalFilename(), cvFile.getSize());
        Job job = getJobOrThrow(jobId);
        val result = handler.handleNewFileCv(cvFile, job);
        return ResponseEntity.ok(result);
    }

    private Job getJobOrThrow(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
    }
}