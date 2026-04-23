package com.onceClick.recruitmentService.features.candidate.handler;

import com.onceClick.recruitmentService.features.candidate.dto.request.CandidateRequestDto;
import com.onceClick.recruitmentService.features.candidate.dto.response.CandidateResponseDto;
import com.onceClick.recruitmentService.features.education.DTO.EducationResponseDto;
import com.onceClick.recruitmentService.features.education.Handler.EducationHandler;
import com.onceClick.recruitmentService.features.experience.DTO.ExperienceResponseDto;
import com.onceClick.recruitmentService.features.experience.Handler.ExperienceHandler;
import com.onceClick.recruitmentService.features.skill.handler.SkillHandler;
import com.onceClick.recruitmentService.infrastructure.feign.AuthAccountDto;
import com.onceClick.recruitmentService.infrastructure.feign.SyncDataFromAccountHandler;
import com.onceClick.recruitmentService.infrastructure.storage.CloudinaryStorageService.CloudinaryStorageService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateSkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateProfileHandler {

    private final CandidateRepository candidateRepository;
    private final SyncDataFromAccountHandler syncDataFromAccountHandler;
    private final CloudinaryStorageService cloudinaryStorageService;
    private final SkillHandler skillHandler;
    private final CandidateSkillRepository candidateSkillRepository;
    private final EducationHandler educationHandler;
    private final ExperienceHandler experienceHandler;

    //-----------------------------------------------------------------------------------------------------------------------------------------
    // UPDATE PROFILE
    //-----------------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public ApiResponse<CandidateResponseDto> updateCandidateProfile(CandidateRequestDto requestDto, UUID candidateId)
    {

        // 1. Check exist candidate
        Candidate candidate = getOrSyncCandidate(candidateId);

        // 2. Update field from request
        updateNullableFields(candidate, requestDto);

        // 3. Save into table Candidate of Recruitment DB
        candidateRepository.save(candidate);
        log.info("Updated candidate profile: {}", candidateId);

        if (requestDto.getSkills() != null) {
            skillHandler.replaceSkillsForCandidate(candidateId, requestDto.getSkills());
        }

        if (requestDto.getExperiences() != null) {
            experienceHandler.replaceAllExperiences(candidateId, requestDto.getExperiences());
        }

        // 5. return ApiResponse
        return ApiResponse.<CandidateResponseDto>builder()
                .success(true)
                .message("Candidate profile updated successfully!")
                .data(mapToResponseDto(candidate))
                .build();

    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    // GET PROFILE WITH CANDIDATE ID
    //-----------------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public ApiResponse<CandidateResponseDto> findByCandidateId(UUID candidateId){

        Candidate candidate = getOrSyncCandidate(candidateId);

        return ApiResponse.<CandidateResponseDto>builder()
                .success(true)
                .message("Candidate profile was filled successfully!")
                .data(mapToResponseDto(candidate))
                .build();
    }


    // -------------------------------------------------------------------------
    // UPDATE AVATAR
    // -------------------------------------------------------------------------
    @Transactional
    public ApiResponse<CandidateResponseDto> updateAvatar (UUID candidateId, MultipartFile file) throws IOException {

        Candidate candidate = getOrSyncCandidate(candidateId);

        String oldAvatarUrl = candidate.getAvatarUrl();
        if(oldAvatarUrl != null){
            String oldPublicId = extractPublicId(oldAvatarUrl);
            try{
                cloudinaryStorageService.deleteImage(oldPublicId);
            } catch (IOException e){
                log.warn("Failed to delete old avatar {}: {}", oldPublicId, e.getMessage());
            }
        }

        String url = cloudinaryStorageService.uploadImage(file, "profiles/avatars");
        candidate.setAvatarUrl(url);
        candidate.setUpdatedAt(Instant.now());
        candidateRepository.save(candidate);

        log.info("Updated avatar for candidate: {}", candidateId);

        return ApiResponse.<CandidateResponseDto>builder()
                .success(true)
                .message("Avatar updated successfully!")
                .data(mapToResponseDto(candidate))
                .build();
    }


    // -------------------------------------------------------------------------
    // DELETE AVATAR
    // -------------------------------------------------------------------------
    @Transactional
    public ApiResponse<Void> deleteAvatar(UUID candidateId) throws IOException{

        Candidate candidate = getOrSyncCandidate(candidateId);

        String oldAvatarUrl = candidate.getAvatarUrl();
        if(oldAvatarUrl != null){
            String oldPublicId = extractPublicId(oldAvatarUrl);
            try{
                cloudinaryStorageService.deleteImage(oldPublicId);
                candidate.setAvatarUrl(null);
                candidate.setUpdatedAt(Instant.now());
            } catch (IOException e){
                log.warn("Failed to delete old avatar {}: {}", oldPublicId, e.getMessage());
            }
        }

        candidateRepository.save(candidate);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Candidate avatar deleted successfully!")
                .build();
    }


    // -------------------------------------------------------------------------
    // UPDATE COVER
    // -------------------------------------------------------------------------
    @Transactional
    public ApiResponse<CandidateResponseDto> updateBackground (UUID candidateId, MultipartFile file) throws IOException {

        Candidate candidate = getOrSyncCandidate(candidateId);

        String oldBackgroundUrl = candidate.getBackgroundUrl();
        if(oldBackgroundUrl != null){
            String oldPublicId = extractPublicId(oldBackgroundUrl);
            try{
                cloudinaryStorageService.deleteImage(oldPublicId);
            } catch (IOException e){
                log.warn("Failed to delete old background {}: {}", oldPublicId, e.getMessage());
            }
        }

        String url = cloudinaryStorageService.uploadImage(file, "profiles/covers");
        candidate.setBackgroundUrl(url);
        candidate.setUpdatedAt(Instant.now());
        candidateRepository.save(candidate);

        log.info("Updated Background Image for candidate: {}", candidateId);

        return ApiResponse.<CandidateResponseDto>builder()
                .success(true)
                .message("Background image updated successfully!")
                .data(mapToResponseDto(candidate))
                .build();
    }



    // -------------------------------------------------------------------------
    // DELETE BACKGROUND
    // -------------------------------------------------------------------------
    @Transactional
    public ApiResponse<Void> deleteBackground(UUID candidateId) throws IOException{

        Candidate candidate = getOrSyncCandidate(candidateId);

        String oldBackgroundUrl = candidate.getBackgroundUrl();
        if(oldBackgroundUrl != null){
            String oldPublicId = extractPublicId(oldBackgroundUrl);
            try{
                cloudinaryStorageService.deleteImage(oldPublicId);
                candidate.setBackgroundUrl(null);
                candidate.setUpdatedAt(Instant.now());
            } catch (IOException e){
                log.warn("Failed to delete old background {}: {}", oldPublicId, e.getMessage());
            }
        }

        candidateRepository.save(candidate);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Candidate background deleted successfully!")
                .build();
    }

    // -------------------------------------------------------------------------
    // HELPER EXTRACT PUBLIC ID FROM CLOUDINARY URL
    // -------------------------------------------------------------------------
    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        try {
            // URL: https://res.cloudinary.com/{cloud}/image/upload/v1775379790/profiles/covers/abc.jpg
            // Cắt lấy phần sau "/upload/"
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                log.warn("Unexpected Cloudinary URL format: {}", imageUrl);
                return null;
            }

            String afterUpload = parts[1];
            // afterUpload = "v1775379790/profiles/covers/abc.jpg"

            // Bỏ version (v + số + /) nếu có ở đầu
            String withoutVersion = afterUpload.replaceAll("^v\\d+/", "");
            // withoutVersion = "profiles/covers/abc.jpg"

            // Bỏ extension (.jpg, .png, .webp...)
            String publicId = withoutVersion.replaceAll("\\.[a-zA-Z0-9]+$", "");
            // publicId = "profiles/covers/abc"

            log.debug("Extracted publicId: '{}' from URL: {}", publicId, imageUrl);
            return publicId;

        } catch (Exception e) {
            log.warn("Cannot extract publicId from URL: {}", imageUrl);
            return null;
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    // HELPER UPDATE NULLABLE FIELDS
    //-----------------------------------------------------------------------------------------------------------------------------------------
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


    //-----------------------------------------------------------------------------------------------------------------------------------------
    // HELPER GET OR SYNC CANDIDATE
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private Candidate getOrSyncCandidate(UUID candidateId){
        return candidateRepository.findById(candidateId)
                .orElseGet(() -> {
                    log.debug("Candidate {} not found, syncing from Auth Service", candidateId);
                    AuthAccountDto authAccount = syncDataFromAccountHandler.syncAccountData(candidateId);

                     Candidate candidate = Candidate.builder()
                            .candidateId(candidateId)
                            .email(authAccount.getEmail())
                            .phone(authAccount.getPhone())
                            .consentVersion("1.0")
                            .status(authAccount.getStatus())
                            .createdAt(Instant.now())
                            .isNew(true)
                            .build();

                     return candidateRepository.save(candidate);
                });
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    // HELPER MAP TO RESPONSE DTO
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private CandidateResponseDto mapToResponseDto(Candidate candidate) {
        List<String> skills = candidateSkillRepository.findSkillNamesByCandidateId(candidate.getCandidateId());
        List<EducationResponseDto> educations = educationHandler.getEducations(candidate.getCandidateId());
        List<ExperienceResponseDto> experiences = experienceHandler.getExperiences(candidate.getCandidateId());
        return CandidateResponseDto.builder()
                .candidateId(candidate.getCandidateId())
                .about(candidate.getAbout())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .surname(candidate.getSurname())
                .name(candidate.getName())
                .birthday(candidate.getBirthday())
                .province(candidate.getProvince())
                .commune(candidate.getCommune())
                .gender(candidate.getGender())
                .avatarUrl(candidate.getAvatarUrl())
                .backgroundUrl(candidate.getBackgroundUrl())
                .referenceLink(candidate.getReferenceLink())
                .consentVersion(candidate.getConsentVersion())
                .cccd(candidate.getCccd())
                .verificationLevel(candidate.getVerificationLevel())
                .status(candidate.getStatus())
                .skills(skills)
                .educations(educations)
                .experiences(experiences)
                .build();
    }
}



