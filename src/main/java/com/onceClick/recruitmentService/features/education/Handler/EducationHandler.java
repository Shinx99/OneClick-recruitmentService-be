package com.onceClick.recruitmentService.features.education.Handler;

import com.onceClick.recruitmentService.features.education.DTO.EducationRequestDto;
import com.onceClick.recruitmentService.features.education.DTO.EducationResponseDto;
import com.onceClick.recruitmentService.infrastructure.storage.CloudinaryStorageService.CloudinaryStorageService;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.entity.CandidateEducation;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateEducationRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EducationHandler {
    private final CandidateEducationRepository educationRepository;
    private final CandidateRepository candidateRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    @Transactional(readOnly = true)
    public List<EducationResponseDto> getEducations(UUID candidateId) {
        return educationRepository.findAllByCandidateCandidateId(candidateId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EducationResponseDto addEducation(UUID candidateId, EducationRequestDto request) {
        Candidate candidate = candidateRepository.getReferenceById(candidateId);
        CandidateEducation education = CandidateEducation.builder()
                .candidate(candidate)
                .schoolName(request.getSchoolName())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isCurrent(request.getIsCurrent())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .referenceLink(request.getReferenceLink())
                .build();
        CandidateEducation saved = educationRepository.save(education);
        return toDto(saved);
    }

    @Transactional
    public EducationResponseDto updateEducation(UUID candidateId, UUID educationId, EducationRequestDto request) {
        CandidateEducation education = educationRepository.findById(educationId)
                .orElseThrow(() -> new RuntimeException("Education not found"));
        // Kiểm tra candidate sở hữu (tùy chọn)
        if (!education.getCandidate().getCandidateId().equals(candidateId)) {
            throw new RuntimeException("Access denied");
        }
        education.setSchoolName(request.getSchoolName());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setIsCurrent(request.getIsCurrent());
        education.setDescription(request.getDescription());
        education.setImageUrl(request.getImageUrl());
        education.setReferenceLink(request.getReferenceLink());
        return toDto(educationRepository.save(education));
    }

    @Transactional
    public void deleteEducation(UUID candidateId, UUID educationId) {
        educationRepository.deleteByCandidateCandidateIdAndEducationId(candidateId, educationId);
    }

    @Transactional
    public String uploadEducationImage(UUID educationId, UUID candidateId, MultipartFile file) throws IOException {
        CandidateEducation education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        // Kiểm tra quyền sở hữu (chỉ chủ sở hữu mới được upload ảnh)
        if (!education.getCandidate().getCandidateId().equals(candidateId)) {
            throw new SecurityException("Access denied: You do not own this education record");
        }

        // Xóa ảnh cũ nếu có (tùy chọn - có thể dùng phương thức riêng hoặc xử lý tại đây)
        String oldImageUrl = education.getImageUrl();
        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            String oldPublicId = extractPublicIdFromUrl(oldImageUrl);
            try {
                cloudinaryStorageService.deleteImage(oldPublicId);
                log.info("Deleted old education image for educationId: {}", educationId);
            } catch (IOException e) {
                log.warn("Failed to delete old image (continuing): {}", e.getMessage());
            }
        }

        // Upload ảnh mới lên Cloudinary
        String imageUrl = cloudinaryStorageService.uploadImage(file, "educations/certificates");
        education.setImageUrl(imageUrl);
        education.setUpdatedAt(Instant.now());
        educationRepository.save(education);

        log.info("Uploaded education image for educationId: {}, URL: {}", educationId, imageUrl);
        return imageUrl;
    }

    @Transactional
    public void deleteEducationImage(UUID educationId, UUID candidateId) throws IOException {
        CandidateEducation education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));

        if (!education.getCandidate().getCandidateId().equals(candidateId)) {
            throw new SecurityException("Access denied: You do not own this education record");
        }

        String imageUrl = education.getImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            String publicId = extractPublicIdFromUrl(imageUrl);
            cloudinaryStorageService.deleteImage(publicId);
            education.setImageUrl(null);
            education.setUpdatedAt(Instant.now());
            educationRepository.save(education);
            log.info("Deleted education image for educationId: {}", educationId);
        } else {
            log.info("No image to delete for educationId: {}", educationId);
        }
    }

    // Helper extract publicId từ Cloudinary URL (bạn có thể dùng lại hàm từ CandidateProfileHandler hoặc tạo riêng)
    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) return null;
            String afterUpload = parts[1];
            String withoutVersion = afterUpload.replaceAll("^v\\d+/", "");
            return withoutVersion.replaceAll("\\.[a-zA-Z0-9]+$", "");
        } catch (Exception e) {
            log.warn("Could not extract publicId from URL: {}", imageUrl);
            return null;
        }
    }

    private EducationResponseDto toDto(CandidateEducation edu) {
        return EducationResponseDto.builder()
                .educationId(edu.getEducationId())
                .schoolName(edu.getSchoolName())
                .degree(edu.getDegree())
                .fieldOfStudy(edu.getFieldOfStudy())
                .startDate(edu.getStartDate())
                .endDate(edu.getEndDate())
                .isCurrent(edu.getIsCurrent())
                .description(edu.getDescription())
                .imageUrl(edu.getImageUrl())
                .referenceLink(edu.getReferenceLink())
                .build();
    }
}
