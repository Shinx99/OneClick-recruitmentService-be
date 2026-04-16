package com.onceClick.recruitmentService.features.candidate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateResponseDto {

    private UUID candidateId;

    private String about;

    private String email;

    private String phone;

    private String surname;

    private String name;

    private LocalDate birthday;

    private String province;

    private String commune;

    private Boolean gender; // true = nam, false = nữ

    private String avatarUrl;

    private String backgroundUrl;

    private String referenceLink;

    private String consentVersion;

    private String cccd;

    private String verificationLevel;

    private String status;

    private List<String> skills;
}
