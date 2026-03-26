package com.onceClick.recruitmentService.features.ai_cv_matcher;

import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.regex.MatchResult;

@RestController
@RequestMapping("/api/jobs/{jobId}/ai-cv-matcher")
@RequiredArgsConstructor
public class AiCvMatcherController {
    
    /*@PostMapping("/upload")
    public ApiResponse<MatchResult> matchNewCv(
            @PathVariable UUID jobId,
            @RequestPart("cv") MultipartFile cv,  // ✅ MultipartFile
            @RequestPart(required = false) String candidateName) {
        return ApiResponse.success(
            service.matchNewCv(jobId, cv, candidateName));
    }
    
    @PostMapping("/resume/{resumeId}")
    public ApiResponse<MatchResult> matchExisting(
            @PathVariable UUID jobId,
            @PathVariable UUID resumeId) {
        return ApiResponse.success(
            service.matchExisting(jobId, resumeId));
    }*/
}