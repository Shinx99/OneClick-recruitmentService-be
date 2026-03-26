package com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos;

public record PersonalInfo(
        String fullName,
        String email,
        String phoneNumber,
        String location,
        String avatarUrl,
        String linkedinUrl
) {}