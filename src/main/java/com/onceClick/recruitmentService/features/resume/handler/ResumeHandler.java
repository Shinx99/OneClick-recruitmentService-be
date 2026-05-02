package com.onceClick.recruitmentService.features.resume.handler;

import com.onceClick.recruitmentService.features.resume.dto.request.ResumeRequest;
import com.onceClick.recruitmentService.features.resume.dto.response.ResumeResponse;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ParsedData;
import com.onceClick.recruitmentService.infrastructure.storage.CloudinaryStorageService.CloudinaryStorageService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.ResumeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.Normalizer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeHandler {

    private final ResumeRepository resumeRepository;
    private final CandidateRepository candidateRepository;
    private final CloudinaryStorageService cloudinaryStorageService;
    private final ObjectMapper objectMapper;
    private final ResumeCacheHandler resumeCacheHandler;


    //-----------------------------------------------------------------------------------------------------------------------------------------
    // GET ALL RESUME WHICH FIND_JOB = TRUE AND ACTIVE = TRUE
    //-----------------------------------------------------------------------------------------------------------------------------------------
    @Cacheable(
            value = "resumes",
            key = "#keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize"
    )
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<ResumeResponse>> getAllResumes(String keyword, Pageable pageable){

        // Step 1: Normally keyword
        keyword = normalizeForSearch(keyword);

        // Step 2: Query PostgreSQL FTS
        Page<Resume> resumePage = resumeRepository.searchResumes(keyword, pageable);

        // Step 3: Get all candidateIds in resumePage
        List<UUID> candidateIds = resumePage.getContent()
                .stream()
                .map(Resume::getCandidateId)
                .toList();

        // Step 4: Get all candidate in just once time
        Map<UUID, Candidate> candidateMap = candidateRepository.findAllById(candidateIds)
                .stream()
                .collect(Collectors.toMap(Candidate::getCandidateId, c -> c));

        // Step 5: Map Resume -> ResumeResponse, parse parsedData for every single resumes
        Page<ResumeResponse> responsePage = resumePage.map(resume -> {
                ParsedData parsedData = null;
                if(resume.getParsedData() != null){
                    try{
                        parsedData = objectMapper.convertValue(resume.getParsedData(), ParsedData.class);
                    } catch (Exception e) {
                        log.warn("Failed to deserialize parsedData for resume {}: {}", resume.getResumeId(), e.getMessage());
                    }
                }
                return mapToResponseDto(resume, parsedData, candidateMap);
                });

        // Step 6: Wrap into PageResponse
        PageResponse<ResumeResponse> pageResponse = PageResponse.<ResumeResponse>builder()
                .content(responsePage.getContent())
                .pageNumber(responsePage.getNumber())
                .pageSize(responsePage.getSize())
                .totalElements(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .last(responsePage.isLast())
                .build();

        // Step 7: Warp into ApiResponse
        return ApiResponse.<PageResponse<ResumeResponse>>builder()
                .success(true)
                .message("Get resumes successfully!")
                .data(pageResponse)
                .build();
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    // GET RESUME WITH RESUME ID
    //-----------------------------------------------------------------------------------------------------------------------------------------
    // Fetch Active & FindJob = true Resume with resumeId
    @Cacheable(value = "resume:by-resume-id", key = "#resumeId")
    public ApiResponse<ResumeResponse> fetchResumeDataByResumeId(UUID resumeId, HttpServletRequest request){

        // Step 0: Increasing View Count
        incrementViewCount(resumeId, request);

        // 1. Return
        return resumeCacheHandler.fetchResumeDataByResumeId(resumeId, request);
    }



    //-----------------------------------------------------------------------------------------------------------------------------------------
    // GET DEFAULT RESUME WITH CANDIDATE ID
    //-----------------------------------------------------------------------------------------------------------------------------------------
    // Fetch default Resume with candidateId
    @Cacheable(value = "resume:by-candidate-id", key = "#candidateId")
    @Transactional(readOnly = true)
    public ApiResponse<ResumeResponse> fetchResumeDataById(UUID candidateId){

        // 1. Find default resume by candidateId
        Resume resume = resumeRepository.findDefaultResumeByCandidateId(candidateId)
                .orElseThrow(() -> new RuntimeException("Cannot fine default resume by candidateId: " + candidateId));

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
                .message("Default Resume was filled successfully!")
                .data(mapToResponseDto(resume, parsedData, null))
                .build();
    }



    // -------------------------------------------------------------------------
    // UPDATE DEFAULT RESUME
    // -------------------------------------------------------------------------
    @CacheEvict(value = {
            "resume:by-resume-id",
            "resume:by-candidate-id",
            "resumes"
    }, allEntries = true)
    @Transactional
    public ApiResponse<ResumeResponse> updateResume(ResumeRequest request, UUID candidateId){

        // 1. Find default resume by candidateId
        Resume resume = resumeRepository.findDefaultResumeByCandidateId(candidateId)
                .orElseThrow(() -> new RuntimeException("Cannot find default resume by candidateId:  " + candidateId));

        // 2. Update field from request
        updateFields(resume, request);

        // 3. Save into DB
        resumeRepository.save(resume);
        log.info("Updated resume: {}", resume.getResumeId());

        // 4. Return
        return ApiResponse.<ResumeResponse>builder()
                .success(true)
                .message("Resume updated successfully!")
                .build();
    }



    // -------------------------------------------------------------------------
    // UPDATE IMG
    // -------------------------------------------------------------------------
    @CacheEvict(value = {
            "resume:by-resume-id",
            "resume:by-candidate-id",
            "resumes"
    }, allEntries = true)
    @Transactional
    public ApiResponse<ResumeResponse> updateImg (UUID candidateId, MultipartFile file) throws IOException {

        Resume resume = resumeRepository.findDefaultResumeByCandidateId(candidateId)
                .orElseThrow(() -> new RuntimeException("Cannot find img url by candidateId: " + candidateId));

        String oldImg = resume.getImgUrl();

        if(oldImg != null){
            String oldPublicId = extractPublicId(oldImg);
            try{
                cloudinaryStorageService.deleteImage(oldPublicId);
            } catch (IOException e){
                log.warn("Failed to delete old avatar {}: {}", oldPublicId, e.getMessage());
            }
        }

        String url = cloudinaryStorageService.uploadImage(file, "resumes/covers");
        resume.setImgUrl(url);
        resume.setUpdatedAt(Instant.now());
        resumeRepository.save(resume);

        return ApiResponse.<ResumeResponse>builder()
                .success(true)
                .message("Resume img updated successfully!")
                .data(ResumeResponse.builder().imgUrl(url).build())
                .build();
    }


    // -------------------------------------------------------------------------
    // DELETE IMG
    // -------------------------------------------------------------------------
    @CacheEvict(value = {
            "resume:by-resume-id",
            "resume:by-candidate-id",
            "resumes"
    }, allEntries = true)
    @Transactional
    public ApiResponse<Void> deleteImg (UUID candidateId) throws IOException {

        Resume resume = resumeRepository.findDefaultResumeByCandidateId(candidateId)
                .orElseThrow(() -> new RuntimeException("Cannot find img url by candidateId: " + candidateId));

        String oldImg = resume.getImgUrl();

        if(oldImg != null){
            String oldPublicId = extractPublicId(oldImg);

            cloudinaryStorageService.deleteImage(oldPublicId);
            resume.setImgUrl(null);
            resume.setUpdatedAt(Instant.now());

        }

        resumeRepository.save(resume);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Resume img deleted successfully!")
                .build();
    }


    // -------------------------------------------------------------------------
    // HELPER NORMALIZE
    // -------------------------------------------------------------------------
    private String normalizeForSearch(String keyword) {
        if(keyword == null || keyword.trim().isEmpty()) return null;
        return keyword.trim().toLowerCase();
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



    //-----------------------------------------------------------------------------------------------------------------------------------------
    // HELPER UPDATE NULLABLE FIELDS
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void updateFields(Resume resume, ResumeRequest request){

        // 1. Career goal
        if(request.getCareerGoal() != null) resume.setCareerGoal(request.getCareerGoal());

        // 2. Major
        if(request.getMajor() != null) resume.setMajor(request.getMajor());

        // 3. Experience year
        if(request.getExperienceYear() != null) resume.setExperienceYear(request.getExperienceYear());

        // 4. Salary expectation
        if(request.getSalaryExpectation() != null) resume.setSalaryExpectation(request.getSalaryExpectation());

        // 5. Img url
        if(request.getImgUrl() != null) resume.setImgUrl(request.getImgUrl());

        // 6. View count
        if(request.getViewCount() != null) resume.setViewCount(request.getViewCount());

        // 7. Status
        if(request.getStatus() != null) resume.setStatus(request.getStatus());

        // 8. findJob
        if(request.getFindJob() != null) resume.setFindJob(request.getFindJob());

        // 9. ── Lưu parsedData: ParsedData → Map<String, Object> ──
        if (request.getParsedData() != null) {
            Map<String, Object> parsedMap = objectMapper.convertValue(
                    request.getParsedData(), Map.class);
            resume.setParsedData(parsedMap);
        }

        resume.setUpdatedAt(Instant.now());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    // HELPER UPDATE NULLABLE FIELDS
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void incrementViewCount(UUID resumeId, HttpServletRequest request){
        HttpSession session = request.getSession(true);
        String viewKey = "viewed_resume_" + resumeId;

        if(session.getAttribute(viewKey) == null){
            resumeRepository.incrementViewCount(resumeId);
            session.setAttribute(viewKey, true);
            log.debug("Increased view count for resume: {}", resumeId);
        } else {
            log.debug("View count already increased for resume: {} in this session", resumeId);
        }
    }

}
