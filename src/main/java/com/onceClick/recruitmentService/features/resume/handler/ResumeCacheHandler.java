package com.onceClick.recruitmentService.features.resume.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.features.resume.dto.response.ResumeResponse;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ParsedData;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import com.onceClick.recruitmentService.shared.persistence.repository.ResumeRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;



@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeCacheHandler {

    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;
    private final CandidateRepository candidateRepository;


    //-----------------------------------------------------------------------------------------------------------------------------------------
    // GET RESUME WITH RESUME ID
    //-----------------------------------------------------------------------------------------------------------------------------------------
    // Fetch Active & FindJob = true Resume with resumeId
    @Cacheable(value = "resume:by-resume-id", key = "#resumeId")
    @Transactional(readOnly = true)
    public ApiResponse<ResumeResponse> fetchResumeDataByResumeId(UUID resumeId, HttpServletRequest request){

        // 1. Find Active and Findjob by resumeId
        Resume resume = resumeRepository.findResumeByResumeId(resumeId)
                .orElseThrow(() -> new RuntimeException("Can't find resumeId: " + resumeId));

        // 2. ParsedData
        ParsedData parsedData = null;
        if(resume.getParsedData() != null){
            try{
                parsedData = objectMapper.convertValue(resume.getParsedData(), ParsedData.class);
            } catch (Exception e){
                log.warn("Failed to deserialize parsedData for resume {}: {}", resume.getResumeId(), e.getMessage());
            }
        }

        // 3. Return
        return ApiResponse.<ResumeResponse>builder()
                .success(true)
                .message("Resume was filled successfully!")
                .data(mapToResponseDto(resume, parsedData, null))
                .build();
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    // HELPER MAP TO RESPONSE DTO
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private ResumeResponse mapToResponseDto(Resume resume, ParsedData parsedData, Map<UUID, Candidate> candidateMap){

        Candidate candidate = candidateMap != null ? candidateMap.get(resume.getCandidateId())
                : candidateRepository.findById(resume.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Can't find candidateId" + resume.getCandidateId()));

        return ResumeResponse.builder()
                .resumeId(resume.getResumeId())
                .candidateId(resume.getCandidateId())
                .major(resume.getMajor())
                .isDefault(resume.getIsDefault())
                .experienceYear(resume.getExperienceYear())
                .careerGoal(resume.getCareerGoal())
                .salaryExpectation(resume.getSalaryExpectation())
                .viewCount(resume.getViewCount())
                .status(resume.getStatus())
                .findJob(resume.getFindJob())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .deletedAt(resume.getDeletedAt())
                .imgUrl(resume.getImgUrl())
                .parsedData(parsedData)
                .surname(candidate.getSurname())
                .name(candidate.getName())
                .email(candidate.getEmail())
                .build();
    }

}
