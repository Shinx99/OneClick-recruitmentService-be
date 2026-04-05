package com.onceClick.recruitmentService.features.ai_cv_matcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.CvMatchResult;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvDto;
import com.onceClick.recruitmentService.infrastructure.processor.FileProcessorService;
import com.onceClick.recruitmentService.infrastructure.storage.S3StorageService.S3StorageService;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import com.onceClick.recruitmentService.shared.persistence.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiCvMatchHandler {

    private final AiCvMatchService aiCvMatchService;
    private final S3StorageService s3StorageService;
    private final FileProcessorService fileProcessorService;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;


    /**
     *  Luồng 1: Handler cho luồng EXISTING CV (S3 URL)
     */
    public CvMatchResult handleExistingCv(String cvS3Url, Job job) throws Exception {
        log.info("📂 S3 CV full flow: {}", cvS3Url);
        return aiCvMatchService.scanAndMatchCv(cvS3Url, job);
    }

    /**
     * Luồng 1: Cached ParsedCv → Fast match
     */
    // MAIN: resumeId + jobId → Service xử lý cache + match
    public CvMatchResult handleResumeCv(UUID resumeId, Job job) throws Exception {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found: " + resumeId));

        // Cache hit: dùng Map<String, Object> của parsedData
        if (isValidCache(resume.getParsedData())) {
            ParsedCvDto cachedCv = objectMapper.convertValue(resume.getParsedData(), ParsedCvDto.class);
            return aiCvMatchService.matchWithCachedCv(cachedCv, job);
        }

        // Cache miss: full S3 → scan → match
        CvMatchResult result = aiCvMatchService.scanAndMatchCv(resume.getResumeUploadUrl(), job);

        // Cập nhật cache: chuyển ParsedCvDto → Map<String, Object>
        Map<String, Object> parsedDataMap = objectMapper.convertValue(result.getParsedCv(), Map.class);
        resume.setParsedData(parsedDataMap);
        resumeRepository.save(resume);

        return result;
    }
    public CvMatchResult handleCachedCv(ParsedCvDto cachedCv, Job job) throws Exception {
        log.info("CACHE HIT: Fast match only");
        return aiCvMatchService.matchWithCachedCv(cachedCv, job);
    }

    /**
     * Luồng 2: New MultipartFile → Direct extract (3.3s)
     */
    public CvMatchResult handleNewFileCv(MultipartFile cvFile, Job job) throws Exception {
        log.info("📤 New file direct: {} ({}B)",
                cvFile.getOriginalFilename(), cvFile.getSize());

        val cvText = fileProcessorService.extractText(cvFile);
        return aiCvMatchService.scanAndMatchText(cvText, job);
    }

    private boolean isValidCache(Map<String, Object> parsedData) {
        if (parsedData == null || parsedData.isEmpty()) return false;
        try {
            // Dùng ObjectMapper để convert Map → ParsedCvDto rồi kiểm tra
            ParsedCvDto dto = objectMapper.convertValue(parsedData, ParsedCvDto.class);
            return dto.getPersonal() != null
                    && dto.getPersonal().getFullName() != null
                    && dto.getSkills() != null
                    && !dto.getSkills().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *  Luồng 2: Handler cho luồng NEW CV (MultipartFile → temp S3 → process → delete)
     */
    /*
    public CvMatchResult handleNewCv(MultipartFile cvFile, Job job) throws Exception {
        String tempS3Key = "temp/match/" + UUID.randomUUID() + "_" + cvFile.getOriginalFilename();
        String tempS3Url = s3StorageService.uploadTempFile(tempS3Key, cvFile);

        // Validate file
        if (cvFile.isEmpty() || cvFile.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("CV file invalid: size=" + cvFile.getSize());
        }

        String filename = cvFile.getOriginalFilename();
        if (!isSupportedCvFile(filename)) {
            throw new IllegalArgumentException("Unsupported CV format: " + filename);
        }


        try {
            return aiCvMatchService.scanAndMatchCv(tempS3Url, job);
        } finally {
            s3StorageService.deleteTempObject(tempS3Key);
            log.info("🗑️ Auto-deleted temp CV: {}", tempS3Key);
        }
    }

    private boolean isSupportedCvFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf")*//* || lower.endsWith(".docx")*//*;
    }*/
}