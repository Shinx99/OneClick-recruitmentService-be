package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.response.CreateJobResponseDto;
import com.onceClick.recruitmentService.infrastructure.storage.CloudinaryStorageService.CloudinaryStorageService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ForbiddenException;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.entity.JobEmployer;
import com.onceClick.recruitmentService.shared.persistence.entity.JobEmployerId;
import com.onceClick.recruitmentService.shared.persistence.repository.JobEmployerRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadJobImageHandler {

    private final JobRepository jobRepository;
    private final JobEmployerRepository jobEmployerRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    @Transactional
    public ApiResponse<CreateJobResponseDto> uploadJobImage(UUID jobId, UUID employerId, MultipartFile file) throws IOException {

        // 1. Tìm job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        // 2. Kiểm tra quyền owner
        JobEmployerId jobEmployerIdKey = JobEmployerId.builder()
                .jobId(jobId)
                .employerId(employerId)
                .build();

        JobEmployer jobEmployer = jobEmployerRepository.findById(jobEmployerIdKey)
                .orElseThrow(() -> new ForbiddenException("job", "upload image"));

        if (!"owner".equalsIgnoreCase(jobEmployer.getAccessRole())) {
            throw new ForbiddenException("Only job owner can upload image for this job");
        }

        // 3. Xóa ảnh cũ trên Cloudinary nếu có
        String oldImgUrl = job.getImgUrl();
        if (oldImgUrl != null && !oldImgUrl.isBlank()) {
            String oldPublicId = extractPublicId(oldImgUrl);
            try {
                cloudinaryStorageService.deleteImage(oldPublicId);
            } catch (IOException e) {
                log.warn("Failed to delete old job image {}: {}", oldPublicId, e.getMessage());
            }
        }

        // 4. Upload ảnh mới lên Cloudinary → folder "jobs/images"
        String imgUrl = cloudinaryStorageService.uploadImage(file, "jobs/covers");
        job.setImgUrl(imgUrl);
        job.setUpdatedBy(employerId);
        jobRepository.save(job);

        log.info("Updated image for job: {}", jobId);

        // 5. Trả response
        CreateJobResponseDto response = new CreateJobResponseDto(
                job.getJobId(),
                job.getTitle(),
                job.getStatus(),
                job.getImgUrl(),
                job.getCompanyId(),
                job.getApplicationCount(),
                job.getViewCount()
        );

        return ApiResponse.success("Job image uploaded successfully!", response);
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                log.warn("Unexpected Cloudinary URL format: {}", imageUrl);
                return null;
            }
            String afterUpload = parts[1];
            String withoutVersion = afterUpload.replaceAll("^v\\d+/", "");
            return withoutVersion.replaceAll("\\.[a-zA-Z0-9]+$", "");
        } catch (Exception e) {
            log.warn("Cannot extract publicId from URL: {}", imageUrl);
            return null;
        }
    }
}
