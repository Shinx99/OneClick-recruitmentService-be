package com.onceClick.recruitmentService.features.ai_cv_matcher.dto;// shared/dto/ai-cv-matcher/

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CvUploadRequest {
    @NotNull
    @Size(max = 10_000_000)  // 10MB
    MultipartFile cv;  // Controller @RequestPart
    
    @Size(max = 100)
    String candidateName;  // Optional metadata
}

// Existing resume chỉ cần PathVar, không cần body DTO