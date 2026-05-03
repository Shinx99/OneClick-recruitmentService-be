package com.onceClick.recruitmentService.features.admin.user.candidate.Handler;

import com.onceClick.recruitmentService.features.admin.user.candidate.DTO.CandidateDetailResponse;
import com.onceClick.recruitmentService.features.admin.user.candidate.DTO.CandidateListResponse;
import com.onceClick.recruitmentService.features.education.DTO.EducationResponseDto;
import com.onceClick.recruitmentService.features.education.Handler.EducationHandler;
import com.onceClick.recruitmentService.features.experience.DTO.ExperienceResponseDto;
import com.onceClick.recruitmentService.features.experience.Handler.ExperienceHandler;
import com.onceClick.recruitmentService.infrastructure.feign.AuthServiceClient;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateAdminHandler {

    private final CandidateRepository candidateRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final EducationHandler educationHandler;
    private final ExperienceHandler experienceHandler;
    private final AuthServiceClient authServiceClient;

    @Transactional(readOnly = true)
    public Page<CandidateListResponse> getCandidates(String status, String keyword, Pageable pageable) {
        String keywordParam = null;
        if (keyword != null && !keyword.isBlank()) {
            keywordParam = "%" + keyword.trim().toLowerCase() + "%";
        }
        return candidateRepository
                .findFilteredCandidates(status, keywordParam, pageable)
                .map(this::toListResponse);
    }

    @Transactional(readOnly = true)
    public CandidateDetailResponse getCandidateDetail(UUID candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        return toDetailResponse(candidate);
    }

    @Transactional
    public void updateCandidateStatus(UUID candidateId, String newStatus) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        candidate.setStatus(newStatus);
        candidateRepository.save(candidate);

        authServiceClient.updateAccountStatus(candidateId, newStatus);
    }

    private CandidateListResponse toListResponse(Candidate c) {
        return CandidateListResponse.builder()
                .candidateId(c.getCandidateId())
                .surname(c.getSurname())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .province(c.getProvince())
                .status(c.getStatus())
                .birthday(c.getBirthday())
                .verified(c.getCccdVerifiedAt() != null)
                .build();
    }

    private CandidateDetailResponse toDetailResponse(Candidate c) {
        List<String> skills = candidateSkillRepository.findSkillNamesByCandidateId(c.getCandidateId());
        List<EducationResponseDto> educations = educationHandler.getEducations(c.getCandidateId());
        List<ExperienceResponseDto> experiences = experienceHandler.getExperiences(c.getCandidateId());

        return CandidateDetailResponse.builder()
                .candidateId(c.getCandidateId())
                .email(c.getEmail())
                .phone(c.getPhone())
                .surname(c.getSurname())
                .name(c.getName())
                .birthday(c.getBirthday())
                .province(c.getProvince())
                .commune(c.getCommune())
                .gender(c.getGender())
                .about(c.getAbout())
                .avatarUrl(c.getAvatarUrl())
                .backgroundUrl(c.getBackgroundUrl())
                .referenceLink(c.getReferenceLink())
                .consentVersion(c.getConsentVersion())
                .cccd(c.getCccd())
                .cccdVerifiedAt(c.getCccdVerifiedAt())
                .verificationLevel(c.getVerificationLevel())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .skills(skills)
                .educations(educations)
                .experiences(experiences)
                .build();
    }
}
