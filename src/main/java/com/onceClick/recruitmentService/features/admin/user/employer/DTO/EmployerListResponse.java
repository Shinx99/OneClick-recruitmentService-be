package com.onceClick.recruitmentService.features.admin.user.employer.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmployerListResponse {
    private UUID employerId;
    private String surname;
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String status;
}
