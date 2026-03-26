// DownloadCvHandler.java
package com.onceClick.recruitmentService.features.downloadCv;

import com.onceClick.recruitmentService.features.downloadCv.dto.CvDto;
import com.onceClick.recruitmentService.features.downloadCv.dto.CvResponse;
import com.onceClick.recruitmentService.infrastructure.storage.S3StorageService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadCvHandler {

    private final ResumeRepository resumeRepo;


    @Qualifier("s3StorageService")
    private final S3StorageService storageService;  // Interface!

    public CvResponse listCvs(UUID accountId) {
        List<S3Object> objects = storageService.listCvObjects(accountId);

        // Record: new CvResponse(params...)
        List<CvDto> cvDtos = objects.stream()
                .map(this::toCvDto)  // ✅ Record constructor!
                .toList();
        return new CvResponse(cvDtos, cvDtos.size());  // ✅ No builder!
    }


    // DownloadCvHandler.toCvDto()
    private CvDto toCvDto(S3Object obj) {
        String filename = obj.key().substring(obj.key().lastIndexOf('/') + 1);
        return new CvDto(
                null,
                filename,
                obj.size(),
                Instant.ofEpochMilli(obj.lastModified().toEpochMilli()),
                obj.key(),
                "/api/profile/cv/" + filename,
                "/api/profile/cv/stream/" + filename
        );
    }

    public ApiResponse<String> setDefault(UUID candidateId, UUID resumeId) {
        log.info("Set default CV: resumeId={}, candidateId={}", resumeId, candidateId);

        try {
            // 1. Reset tất cả CV khác
            resumeRepo.resetDefaultForCandidate(candidateId);
            log.debug("Reset default for candidate: {}", candidateId);

            // 2. Set CV mới làm default
            int updated = resumeRepo.setDefault(resumeId, candidateId);

            if (updated == 0) {
                log.warn("CV not found/unauthorized: resumeId={}, candidateId={}", resumeId, candidateId);
                // success(String message, T data=null)
                return ApiResponse.success("CV không tồn tại hoặc không thuộc bạn!", null);
            }

            log.info("Set default success: {}", resumeId);
            // success(String message, T data=null)
            return ApiResponse.success("Đã set CV mặc định thành công!", null);

        } catch (Exception e) {
            log.error("Set default failed: resumeId={}, candidateId={}", resumeId, candidateId, e);
            // error(status, message, path)
            return ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage(), "/api/profile/cv/default");
        }
    }
}





















    /*private CvDto toCvDto(S3Object obj) {
        String filename = obj.key().substring(obj.key().lastIndexOf('/') + 1);
        return new CvDto(
                null,
                filename,
                obj.size(),
                Instant.ofEpochMilli(obj.lastModified().toEpochMilli()),
                obj.key()
        );
    }*/