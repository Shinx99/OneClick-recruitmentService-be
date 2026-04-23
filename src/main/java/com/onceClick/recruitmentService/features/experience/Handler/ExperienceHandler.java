package com.onceClick.recruitmentService.features.experience.Handler;

import com.onceClick.recruitmentService.features.experience.DTO.ExperienceRequestDto;
import com.onceClick.recruitmentService.features.experience.DTO.ExperienceResponseDto;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.entity.CandidateExperience;
import com.onceClick.recruitmentService.shared.persistence.entity.Experience;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateExperienceRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperienceHandler {
    private final ExperienceRepository experienceRepository;
    private final CandidateExperienceRepository candidateExperienceRepository;
    private final CandidateRepository candidateRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<ExperienceResponseDto> getExperiences(UUID candidateId) {
        List<Experience> experiences = candidateExperienceRepository.findAllExperiencesByCandidateId(candidateId);
        return experiences.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void replaceAllExperiences(UUID candidateId, List<ExperienceRequestDto> dtos) {
        Candidate candidate = candidateRepository.getReferenceById(candidateId);

        // Xóa toàn bộ liên kết cũ (Experience sẽ bị xóa nếu không còn liên kết với candidate nào khác - orphanRemoval)
        candidateExperienceRepository.deleteAllByCandidateId(candidateId);

        // Tạo mới danh sách Experience và liên kết
        for (ExperienceRequestDto dto : dtos) {
            Experience experience = Experience.builder()
                    .company(dto.getCompanyId() != null ? companyRepository.getReferenceById(dto.getCompanyId()) : null)
                    .customCompanyName(dto.getCustomCompanyName())
                    .headline(dto.getHeadline())
                    .employmentType(dto.getEmploymentType())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .description(dto.getDescription())
                    .locationType(dto.getLocationType())
                    .employmentLocation(dto.getEmploymentLocation())
                    .employmentIndustry(dto.getEmploymentIndustry())
                    .isCurrent(dto.getIsCurrent())
                    .build();
            experience = experienceRepository.save(experience);

            CandidateExperience link = CandidateExperience.builder()
                    .id(new CandidateExperience.CandidateExperienceId(candidateId, experience.getExperienceId()))
                    .candidate(candidate)
                    .experience(experience)
                    .build();
            candidateExperienceRepository.save(link);
        }
        log.info("Replaced {} experiences for candidate {}", dtos.size(), candidateId);
    }

    private ExperienceResponseDto toDto(Experience exp) {
        String companyName = exp.getCompany() != null ? exp.getCompany().getCompanyName() : exp.getCustomCompanyName();
        return ExperienceResponseDto.builder()
                .experienceId(exp.getExperienceId())
                .companyId(exp.getCompany() != null ? exp.getCompany().getCompanyId() : null)
                .customCompanyName(exp.getCustomCompanyName())
                .companyName(companyName)
                .headline(exp.getHeadline())
                .employmentType(exp.getEmploymentType())
                .startDate(exp.getStartDate())
                .endDate(exp.getEndDate())
                .description(exp.getDescription())
                .locationType(exp.getLocationType())
                .employmentLocation(exp.getEmploymentLocation())
                .employmentIndustry(exp.getEmploymentIndustry())
                .isCurrent(exp.getIsCurrent())
                .build();
    }
}
