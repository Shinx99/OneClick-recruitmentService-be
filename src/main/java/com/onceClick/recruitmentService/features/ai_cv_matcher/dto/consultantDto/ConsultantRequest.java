// ConsultantRequest.java
package com.onceClick.recruitmentService.features.ai_cv_matcher.dto.consultantDto;

import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvDto;
import lombok.Data;

import java.util.UUID;

@Data
public class ConsultantRequest {
    private UUID jobId;
    private ParsedCvDto parsedCv;  // Dùng chính DTO từ match result
    private String question;
    private String sessionId;
}