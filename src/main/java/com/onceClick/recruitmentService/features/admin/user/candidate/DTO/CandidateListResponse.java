package com.onceClick.recruitmentService.features.admin.user.candidate.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CandidateListResponse {
    private UUID candidateId;
    private String surname;
    private String name;
    private String email;
    private String phone;
    private String province;
    private String status;
    private LocalDate birthday;
    private boolean verified;
}
