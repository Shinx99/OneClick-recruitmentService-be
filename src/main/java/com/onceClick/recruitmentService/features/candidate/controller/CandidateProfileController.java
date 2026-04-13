package com.onceClick.recruitmentService.features.candidate.controller;


import com.cloudinary.Api;
import com.onceClick.recruitmentService.features.candidate.dto.request.CandidateRequestDto;
import com.onceClick.recruitmentService.features.candidate.dto.response.CandidateResponseDto;
import com.onceClick.recruitmentService.features.candidate.handler.CandidateProfileHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/candidate/profile")
public class CandidateProfileController {

    private final CandidateProfileHandler candidateProfileHandler;
    private final CurrentUser currentUser;

    // -------------------------------------------------------------------------
    // UPDATE CANDIDATE CONTROLLER
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> updateCandidateProfile(@RequestBody CandidateRequestDto candidateRequest){

        UUID candidateId = currentUser.getCurrentAccountId();

        ApiResponse<CandidateResponseDto> response = candidateProfileHandler.updateCandidateProfile(candidateRequest, candidateId);
        return ResponseEntity.ok(response);
    }


    // -------------------------------------------------------------------------
    // FETCH DATA CANDIDATE CONTROLLER
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @GetMapping("/fetchData")
    public ResponseEntity<ApiResponse<CandidateResponseDto>> fetchCandidateProfile(){

        UUID candidateId = currentUser.getCurrentAccountId();

        ApiResponse<CandidateResponseDto> response = candidateProfileHandler.findByCandidateId(candidateId);
        return ResponseEntity.ok(response);
    }


    // -------------------------------------------------------------------------
    // UPDATE AVATAR IMAGE CANDIDATE CONTROLLER
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @PutMapping(value = "/avatar/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CandidateResponseDto>> updateAvatar(@RequestParam("avatarImage") MultipartFile file) throws IOException {

        UUID candidateId = currentUser.getCurrentAccountId();

        ApiResponse<CandidateResponseDto> response = candidateProfileHandler.updateAvatar(candidateId, file);
        return ResponseEntity.ok(response);
    }


    // -------------------------------------------------------------------------
    // UPDATE BACKGROUND IMAGE CANDIDATE CONTROLLER
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @PutMapping(value = "/background/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CandidateResponseDto>> updateBackground(@RequestParam("backgroundImage") MultipartFile file) throws IOException {

        UUID candidateId = currentUser.getCurrentAccountId();

        ApiResponse<CandidateResponseDto> response = candidateProfileHandler.updateBackground(candidateId, file);
        return ResponseEntity.ok(response);
    }


}
