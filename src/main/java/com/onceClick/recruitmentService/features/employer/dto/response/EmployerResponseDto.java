package com.onceClick.recruitmentService.features.employer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerResponseDto {

    UUID employerId;
    String message;

}
