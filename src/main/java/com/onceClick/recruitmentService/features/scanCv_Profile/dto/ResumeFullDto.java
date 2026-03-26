package com.onceClick.recruitmentService.features.scanCv_Profile.dto;

import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ParsedData;

import java.util.UUID;

public record ResumeFullDto(
    UUID resumeId,
    String resumeUploadUrl,
    String imgUrl,
    ParsedData parsedData,
    String scanStatus,
    Double confidenceScore
) {}