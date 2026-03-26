package com.onceClick.recruitmentService.features.scanCv_Profile.dto;

import org.springframework.web.multipart.MultipartFile;

public record ScanCvRequest(
    MultipartFile resumeFile
) {}