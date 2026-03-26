package com.onceClick.recruitmentService.features.scanCv_Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ParsedResumeResponse;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.ScanCvRequest;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ExtractedFields;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ParsedData;
import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.PersonalInfo;
import com.onceClick.recruitmentService.infrastructure.ai.AiService;
import com.onceClick.recruitmentService.infrastructure.file.FileTextExtractor;
import com.onceClick.recruitmentService.infrastructure.file.S3FileProcessorService;
import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateEducationRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanCvHandler {

    private final @Qualifier("s3FileProcessorService") FileTextExtractor extractor;        // ← FIX 3: Interface!
    private final S3FileProcessorService s3Service;   // ← Keep cho uploadCv()
    private final AiService aiService;
    private final ResumeRepository resumeRepo;
    private final CandidateEducationRepository eduRepo;
    private final ObjectMapper objectMapper;

    public ParsedResumeResponse handle(UUID candidateId, ScanCvRequest req) {
        try {
            // 1. Upload trước → lấy S3 URL
            String pdfUrl = s3Service.uploadCv(req.resumeFile(), candidateId);

            // 2. Extract từ S3 URL
            String cvText = extractor.extractTextFromS3(pdfUrl);

            // 3. Parse AI & save như cũ
            String parsedJson = aiService.parseCvText(cvText);

            log.info("AI Raw response: {}", parsedJson.substring(0, 500));

            String cleanJson = parsedJson
                    .replaceAll("```json\\s*", "")  // Remove ```json
                    .replaceAll("```\\s*", "")      // Remove ```
                    .replaceAll("^\\*+\\s*", "")    // Remove leading **
                    .trim();

            ParsedData parsed = aiService.parseResponse(cleanJson, ParsedData.class);
            // FULL mapping theo Entity
            Resume resume = createResume(candidateId, pdfUrl, parsed);
            resumeRepo.save(resume);

            //saveEducations(req.candidateId(), parsed.education());

            return ParsedResumeResponse.success(resume.getResumeId(), pdfUrl, parsed);

        } catch (Exception e) {
            log.error("ScanCV failed: {}", e.getMessage(), e);
            return ParsedResumeResponse.error(e.getMessage());
        }
    }

    /*private Resume createResume(UUID candidateId, String pdfUrl, ParsedData parsed) {
        return Resume.builder()
                .candidateId(candidateId)
                .resumeUploadUrl(pdfUrl)
                .imgUrl(parsed.personal().avatarUrl())  // Từ parsed data
                .parsedData(objectMapper.convertValue(parsed, Map.class))
                .careerGoal(parsed.extractedFields().careerGoal())
                .major(parsed.extractedFields().major())
                .experienceYear(parsed.extractedFields().experienceYear())
                .salaryExpectation(parsed.extractedFields().salaryExpectation())
                .viewCount(0)
                .status("active")
                .build();
    }*/

    private Resume createResume(UUID candidateId, String pdfUrl, ParsedData parsed) {
        ExtractedFields extracted = parsed.extractedFields();

        return Resume.builder()
                .candidateId(candidateId)
                .resumeUploadUrl(pdfUrl)
                .imgUrl(Optional.ofNullable(parsed.personal())
                        .map(PersonalInfo::avatarUrl)
                        .orElse(null))  // Null-safe personal.avatarUrl()
                .parsedData(objectMapper.convertValue(parsed, Map.class))
                .careerGoal(Optional.ofNullable(extracted)
                        .map(ExtractedFields::careerGoal)
                        .orElse(""))     // ✅ Fallback empty string
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

    /*private void saveEducations(UUID candidateId, List<EducationInfo> educations) {
        if (educations == null || educations.isEmpty()) return;

        List<CandidateEducation> eduEntities = educations.stream()
                .map(edu -> CandidateEducation.builder()
                        .candidateId(candidateId)
                        .schoolName(edu.schoolName())
                        .degree(edu.degree())
                        .fieldOfStudy(edu.fieldOfStudy())
                        .startDate(edu.startDate())
                        .endDate(edu.endDate())
                        .isCurrent(edu.isCurrent())
                        .build())
                .toList();

        eduRepo.saveAll(eduEntities);
    }*/

}