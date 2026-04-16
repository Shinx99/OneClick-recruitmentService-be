package com.onceClick.recruitmentService.features.scanCv_Profile;

import com.cloudinary.Api;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ParsedResumeResponse;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ScanCvRequest;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ExtractedFields;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ParsedData;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.PersonalInfo;
import com.onceClick.recruitmentService.infrastructure.ai.AiService;
import com.onceClick.recruitmentService.infrastructure.processor.S3FileExtractorService;
import com.onceClick.recruitmentService.infrastructure.storage.S3StorageService.S3StorageService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateEducationRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanCvHandler {

    private final S3FileExtractorService extractor;
    private final S3StorageService s3Service;
    private final AiService aiService;
    private final ResumeRepository resumeRepo;
    private final CandidateEducationRepository eduRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public ParsedResumeResponse handle(UUID candidateId, ScanCvRequest req) {
        try {
            // 1. Upload trước → lấy S3 URL
            String pdfUrl = s3Service.uploadCv(req.resumeFile(), candidateId);

            // 2. Extract từ S3 URL
            String cvText = extractor.extractTextFromS3(pdfUrl);

            if (cvText.startsWith("[IMAGE_SCAN_CV]")) {
                log.warn("🚨 Image CV detected: {}", pdfUrl);
                return ParsedResumeResponse.warning(  // Hoặc constructor
                        cvText + "\n💡 Fix: Mở Word → File → Export → Create PDF (text-selectable)"
                );
            }

            // 3. Parse AI & save như cũ
            String parsedJson = aiService.parseCvText(cvText);

            log.info("AI Raw response: {}", parsedJson.substring(0, 500));

            String cleanJson = parsedJson
                    .replaceAll("```json\\s*", "")  // Remove ```json
                    .replaceAll("```\\s*", "")      // Remove ```
                    .replaceAll("^\\*+\\s*", "")    // Remove leading **
                    .trim();

            ParsedData parsed = aiService.parseResponse(cleanJson, ParsedData.class);

            Resume resume = createResume(candidateId, pdfUrl, parsed, false);
            resume = resumeRepo.save(resume);

            // 2. Reset all old TRUE
            resumeRepo.setCandidateResumesNotDefault(candidateId);

            // 3. Set new TRUE (constraint OK vì chỉ còn FALSE)
            resumeRepo.setDefault(resume.getResumeId(), candidateId);

            //saveEducations(req.candidateId(), parsed.education());

            return ParsedResumeResponse.success(resume.getResumeId(), pdfUrl, parsed);

        } catch (Exception e) {
            log.error("ScanCV failed: {}", e.getMessage(), e);
            return ParsedResumeResponse.error(e.getMessage());
        }
    }

    private Resume createResume(UUID candidateId, String pdfUrl, ParsedData parsed,  boolean isDefault) {
        ExtractedFields extracted = parsed.extractedFields();

        return Resume.builder()
                .candidateId(candidateId)
                .isDefault(isDefault)
                .resumeUploadUrl(pdfUrl)
                .imgUrl(Optional.ofNullable(parsed.personal())
                        .map(PersonalInfo::avatarUrl)
                        .orElse(null))  // Null-safe personal.avatarUrl()
                .parsedData(objectMapper.convertValue(parsed, Map.class))
                .careerGoal(Optional.ofNullable(extracted)
                        .map(ExtractedFields::careerGoal)
                        .orElse(""))     // Fallback empty string
                .major(Optional.ofNullable(extracted)
                        .map(ExtractedFields::major)
                        .orElse(""))
                .experienceYear(Optional.ofNullable(extracted)
                        .map(ExtractedFields::experienceYear)
                        .orElse(BigDecimal.ZERO))
                .salaryExpectation(Optional.ofNullable(extracted)
                        .map(ExtractedFields::salaryExpectation)
                        .orElse(null))  // Null cho optional field
                .viewCount(0)
                .status("active")
                .build();
    }

}