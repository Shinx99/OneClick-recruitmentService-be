package com.onceClick.recruitmentService.features.downloadCv;

import com.onceClick.recruitmentService.features.downloadCv.dto.CvResponse;
import com.onceClick.recruitmentService.infrastructure.storage.S3StorageService.S3StorageService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class DownloadCvController {

    private final DownloadCvHandler downloadCvHandler;
    private final S3StorageService storageService;
    private final CurrentUser currentUser;

    // List all CVs
    @GetMapping("/cvs")
    public ApiResponse<CvResponse> listCvs() {
        UUID accountId = currentUser.getCurrentAccountId();
        return ApiResponse.success(downloadCvHandler.listCvs(accountId));
    }

    // Download single CV
    @GetMapping("/cv/{filename:.+}")
    public ResponseEntity<Resource> downloadCv(@PathVariable String filename) {
        UUID accountId = currentUser.getCurrentAccountId();

        log.info("Download CV: accountId={}, filename={}", accountId, filename);

        InputStream inputStream = storageService.downloadCvStream(accountId, filename);
        InputStreamResource resource = new InputStreamResource(inputStream);

        String contentType = storageService.getContentType(filename);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8))
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(storageService.getFileSize(accountId, filename))
                .body(resource);
    }

    // Stream trực tiếp (large files)
    @GetMapping(value = "/cv/stream/{filename:.+}", produces = "application/pdf")
    public ResponseEntity<Resource> previewCvStream(@PathVariable String filename) {
        UUID accountId = currentUser.getCurrentAccountId();

        InputStream inputStream = storageService.downloadCvStream(accountId, filename);
        InputStreamResource resource = new InputStreamResource(inputStream);

        String contentType = storageService.getContentType(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(storageService.getFileSize(accountId, filename))
                .body(resource);
    }

    @PatchMapping("/cv/{resumeId}/default")
    @Transactional
    public ApiResponse<String> setDefaultCv(@PathVariable UUID resumeId) {
        UUID candidateId = currentUser.getCurrentAccountId();
        return downloadCvHandler.setDefault(candidateId, resumeId);
    }


    /**
     * Recruiter xem CV của candidate (có kiểm tra quyền)
     */
    @GetMapping("/cv/stream/{candidateId}/{filename:.+}")
    public ResponseEntity<Resource> previewCandidateCvStream(
            @PathVariable UUID candidateId,
            @PathVariable String filename) {  // Bỏ @AuthenticationPrincipal

        // Lấy employerId từ CurrentUser
        UUID employerId = currentUser.getCurrentAccountId();

        log.info("Recruiter {} viewing CV of candidate {}, filename: {}", employerId, candidateId, filename);

        // Kiểm tra quyền
        boolean hasAccess = downloadCvHandler.checkRecruiterAccessToCandidate(candidateId, employerId);

        if (!hasAccess) {
            log.warn("Recruiter {} has no access to candidate {} CV", employerId, candidateId);
            return ResponseEntity.status(403).build();
        }

        // Lấy CV từ S3
        InputStream inputStream = storageService.downloadCvStream(candidateId, filename);
        InputStreamResource resource = new InputStreamResource(inputStream);

        String contentType = storageService.getContentType(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(storageService.getFileSize(candidateId, filename))
                .body(resource);
    }



}






