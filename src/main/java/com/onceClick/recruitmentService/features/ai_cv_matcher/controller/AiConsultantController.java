// AiConsultantController.java - Thêm vào package features.ai_cv_matcher
package com.onceClick.recruitmentService.features.ai_cv_matcher.controller;

import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.consultantDto.ConsultantRequest;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.consultantDto.ConsultantResponse;
import com.onceClick.recruitmentService.features.ai_cv_matcher.service.AiConsultantService;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/ai-cv-match/consultant")
@RequiredArgsConstructor
public class AiConsultantController {

    private final AiConsultantService consultantService;
    private final JobRepository jobRepository;

    /**
     * Chat tư vấn dựa trên CV đã match và Job
     * Dùng parsedCv từ kết quả match trước đó
     */
    @PostMapping("/chat")
    public ResponseEntity<ConsultantResponse> chat(
            @RequestBody ConsultantRequest request) throws Exception {

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Dùng parsedCv từ request (đã có từ match result)
        ConsultantResponse response = consultantService.chat(
                request.getParsedCv(),
                job,
                request.getQuestion(),
                request.getSessionId()
        );
        return ResponseEntity.ok(response);
    }

    // AiConsultantController.java - Thêm 2 method này

    /**
     * Tạo câu hỏi phỏng vấn dựa trên CV và Job
     */
    @PostMapping("/interview-questions")
    public ResponseEntity<ConsultantResponse> generateInterviewQuestions(@RequestBody ConsultantRequest request) throws Exception {
        log.info("Generate interview questions - jobId: {}", request.getJobId());

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        String questions = consultantService.generateInterviewQuestions(
                request.getParsedCv(),
                job
        );

        return ResponseEntity.ok(ConsultantResponse.builder()
                .content(questions)
                .build());
    }

    /**
     * Gợi ý cải thiện CV dựa trên CV và Job
     */
    @PostMapping("/improve-cv")
    public ResponseEntity<ConsultantResponse> improveCV(@RequestBody ConsultantRequest request) throws Exception {
        log.info("Improve CV advice - jobId: {}", request.getJobId());

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        String advice = consultantService.getImprovementAdvice(
                request.getParsedCv(),
                job,
                request.getQuestion()
        );

        return ResponseEntity.ok(ConsultantResponse.builder()
                .content(advice)
                .build());
    }
}