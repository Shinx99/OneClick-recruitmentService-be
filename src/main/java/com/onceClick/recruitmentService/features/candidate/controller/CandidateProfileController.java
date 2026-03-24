package com.onceClick.recruitmentService.features.candidate.controller;


import com.onceClick.recruitmentService.features.candidate.dto.request.CandidateRequestDto;
import com.onceClick.recruitmentService.features.candidate.dto.response.CandidateResponseDto;
import com.onceClick.recruitmentService.features.candidate.handler.CandidateProfileHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/candidate")
public class CandidateProfileController {

    private final CandidateProfileHandler candidateProfileHandler;

    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> updateCandidateProfile(
            @RequestBody CandidateRequestDto candidateRequest
    ){

        ApiResponse<CandidateResponseDto> response = candidateProfileHandler.updateCandidateProfile(candidateRequest);
        return ResponseEntity.ok(response);

    }

}
