package com.onceClick.recruitmentService.features.candidate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponseDto {

    UUID candidateId;
    String message;
}
