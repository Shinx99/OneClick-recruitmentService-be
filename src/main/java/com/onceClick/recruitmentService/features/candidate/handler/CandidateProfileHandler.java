package com.onceClick.recruitmentService.features.candidate.handler;

import com.onceClick.recruitmentService.features.candidate.dto.request.CandidateRequestDto;
import com.onceClick.recruitmentService.features.candidate.dto.response.CandidateResponseDto;
import com.onceClick.recruitmentService.infrastructure.feign.AuthAccountDto;
import com.onceClick.recruitmentService.infrastructure.feign.SyncDataFromAccountHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateProfileHandler {

    private final CandidateRepository candidateRepository;
    private final SyncDataFromAccountHandler syncDataFromAccountHandler;

    @Transactional
    public ApiResponse<CandidateResponseDto> updateCandidateProfile(CandidateRequestDto requestDto)
    {

        UUID candidateId = requestDto.getCandidateId();

        // 1. Check exist candidate
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);

        // 2. If it doesn't exist, sync from auth service
        if(candidate == null){
            // Not exist, need to sync data from auth service
            log.info("Candidate {} not found, syncing from Auth Service", candidateId);
            AuthAccountDto authAccount = syncDataFromAccountHandler.syncAccountData(candidateId);

            candidate = Candidate.builder()
                    .candidateId(candidateId)
                    .email(authAccount.getEmail())
                    .phone(authAccount.getPhone())
                    .consentVersion("1.0")
                    .status("active")
                    .createdAt(Instant.now())
                    .isNew(true)
                    .build();
        }

        // 3. Update field from request
        updateNullableFields(candidate, requestDto);

        // 4. Save into table Candidate of Recruitment DB
        candidateRepository.save(candidate);
        log.info("Updated candidate profile: {}", candidateId);

        // 5. return ApiResponse
        return ApiResponse.<CandidateResponseDto>builder()
                .success(true)
                .message("Candidate profile updated successfully!")
                .data(new CandidateResponseDto(candidateId, "Candidate profile updated successfully!"))
                .build();

    }


    private void updateNullableFields(Candidate candidate, CandidateRequestDto request){

        //Update every single fields if request doesn't null

        // 1. Field about
        if(request.getAbout() != null) candidate.setAbout(request.getAbout());

        // 2. Field surname
        if(request.getSurname() != null) candidate.setSurname(request.getSurname());

        // 3. Field name
        if(request.getName() != null) candidate.setName(request.getName());

        // 4. Field birthday
        if(request.getBirthday() != null) candidate.setBirthday(request.getBirthday());

        // 5. Field province
        if (request.getProvince() != null) candidate.setProvince(request.getProvince());

        // 6. Field commune
        if (request.getCommune() != null) candidate.setCommune(request.getCommune());

        // 7. Field gender
        if (request.getGender() != null) candidate.setGender(request.getGender());

        // 8. Field avatarUrl
        if (request.getAvatarUrl() != null) candidate.setAvatarUrl(request.getAvatarUrl());

        // 9. Field background
        if (request.getBackgroundUrl() != null) candidate.setBackgroundUrl(request.getBackgroundUrl());

        // 10. Field referenceLink
        if (request.getReferenceLink() != null) candidate.setReferenceLink(request.getReferenceLink());

        // 11. Field cccd
        if (request.getCccd() != null) candidate.setCccd(request.getCccd());

        // 12. Field consentVersion
        if (request.getConsentVersion() != null) candidate.setConsentVersion(request.getConsentVersion());

        // 13. Field verificationLevel
        if (request.getVerificationLevel() != null) candidate.setVerificationLevel(request.getVerificationLevel());

        // 14. Field status
        if (request.getStatus() != null) candidate.setStatus(request.getStatus());

        // System fields
        candidate.setUpdatedAt(Instant.now());

    }





}



