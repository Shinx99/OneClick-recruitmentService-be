package com.onceClick.recruitmentService.features.admin.user.candidate.DTO;

import com.onceClick.recruitmentService.features.education.DTO.EducationResponseDto;
import com.onceClick.recruitmentService.features.experience.DTO.ExperienceResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CandidateDetailResponse {
    private UUID candidateId;
    private String email;
    private String phone;
    private String surname;
    private String name;
    private LocalDate birthday;
    private String province;
    private String commune;
    private Boolean gender;
    private String about;
    private String avatarUrl;
    private String backgroundUrl;
    private String referenceLink;
    private String consentVersion;
    private String cccd;
    private Instant cccdVerifiedAt;
    private String verificationLevel;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    private List<String> skills;
    private List<EducationResponseDto> educations;
    private List<ExperienceResponseDto> experiences;
}
