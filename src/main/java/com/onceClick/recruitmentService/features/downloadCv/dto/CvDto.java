// CvDto.java
package com.onceClick.recruitmentService.features.downloadCv.dto;

import java.time.Instant;
import java.util.UUID;

public record CvDto(
    UUID id,
    String filename,
    long size,
    Instant uploadedAt,
    String s3Key,
    String downloadUrl,
    String streamUrl
) {}