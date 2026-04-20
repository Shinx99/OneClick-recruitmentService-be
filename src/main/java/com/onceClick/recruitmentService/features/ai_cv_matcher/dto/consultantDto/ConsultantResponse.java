// ConsultantResponse.java
package com.onceClick.recruitmentService.features.ai_cv_matcher.dto.consultantDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsultantResponse {
    private String content;
    private String sessionId;
}