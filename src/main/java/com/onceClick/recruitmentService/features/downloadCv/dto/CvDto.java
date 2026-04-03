// CvDto.java
package com.onceClick.recruitmentService.features.downloadCv.dto;

import java.time.Instant;
import java.util.UUID;

public record CvDto(
        UUID resumeId,
        String filename,
        boolean isDefault,
        String status
) {}