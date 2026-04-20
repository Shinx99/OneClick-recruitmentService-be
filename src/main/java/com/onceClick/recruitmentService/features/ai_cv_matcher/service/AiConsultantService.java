// AiConsultantService.java
package com.onceClick.recruitmentService.features.ai_cv_matcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.ParsedCvDto;
import com.onceClick.recruitmentService.features.ai_cv_matcher.dto.consultantDto.ConsultantResponse;
import com.onceClick.recruitmentService.infrastructure.ai.DeepSeekService;
import com.onceClick.recruitmentService.infrastructure.ai.prompt.AiPrompts;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiConsultantService {

    private final DeepSeekService deepSeekService;
    private final ObjectMapper objectMapper;
    private final AiPrompts aiPrompts;
    
    private final Map<String, StringBuilder> sessionContexts = new ConcurrentHashMap<>();

    /**
     * Chat tư vấn dựa trên ParsedCvDto và Job
     */
    public ConsultantResponse chat(ParsedCvDto parsedCv, Job job, String question, String sessionId) throws Exception {
        // Lấy hoặc tạo context cho session
        StringBuilder context = sessionContexts.computeIfAbsent(sessionId, k -> new StringBuilder());
        
        String prompt = buildChatPrompt(parsedCv, job, question, context.toString());
        String response = deepSeekService.chat("consultant_chat", "", prompt);
        
        // Cập nhật context
        context.append("User: ").append(question).append("\n");
        context.append("AI: ").append(response).append("\n");
        
        // Giới hạn context
        if (context.length() > 8000) {
            String trimmed = context.substring(context.length() - 6000);
            sessionContexts.put(sessionId, new StringBuilder(trimmed));
        }
        
        return ConsultantResponse.builder()
                .content(response)
                .sessionId(sessionId)
                .build();
    }

    private String buildChatPrompt(ParsedCvDto cv, Job job, String question, String history) {
        return """
            Bạn là trợ lý AI tư vấn việc làm tại OnceClick.
            
            === THÔNG TIN CV ===
            - Họ tên: %s
            - Kỹ năng: %s
            - Kinh nghiệm: %s
            - Học vấn: %s
            - Mục tiêu: %s
            
            === THÔNG TIN CÔNG VIỆC ===
            %s
            
            === LỊCH SỬ HỘI THOẠI ===
            %s
            
            === CÂU HỎI ===
            %s
            
            Hãy trả lời dựa trên CV và mô tả công việc trên.
            Nếu được hỏi về kỹ năng còn thiếu, hãy tham khảo CV và JD.
            Nếu được hỏi về phỏng vấn, hãy tạo câu hỏi liên quan đến CV và JD.
            Trả lời bằng tiếng Việt, thân thiện, hữu ích.
            """.formatted(
                cv.getPersonal().getFullName(),
                String.join(", ", cv.getSkills()),
                formatExperience(cv),
                formatEducation(cv),
                cv.getExtractedFields() != null ? cv.getExtractedFields().getCareerGoal() : "Chưa xác định",
                aiPrompts.buildJobPrompt(job),
                history != null ? history : "Chưa có lịch sử",
                question
            );
    }

    private String formatExperience(ParsedCvDto cv) {
        if (cv.getWorkExperience() == null || cv.getWorkExperience().isEmpty()) {
            return "Chưa có kinh nghiệm làm việc";
        }
        StringBuilder sb = new StringBuilder();
        for (var exp : cv.getWorkExperience()) {
            sb.append("- ").append(exp.getPosition()).append(" tại ").append(exp.getCompany()).append("\n");
        }
        return sb.toString();
    }

    private String formatEducation(ParsedCvDto cv) {
        if (cv.getEducation() == null || cv.getEducation().isEmpty()) {
            return "Chưa có thông tin học vấn";
        }
        StringBuilder sb = new StringBuilder();
        for (var edu : cv.getEducation()) {
            sb.append("- ").append(edu.getDegree()).append(" tại ").append(edu.getSchoolName()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Tạo câu hỏi phỏng vấn (không lưu DB)
     */
    public String generateInterviewQuestions(ParsedCvDto parsedCv, Job job) throws Exception {
        String prompt = buildInterviewPrompt(parsedCv, job);
        return deepSeekService.chat("interview_questions", "", prompt);
    }

    /**
     * Gợi ý cải thiện CV (không lưu DB)
     */
    public String getImprovementAdvice(ParsedCvDto parsedCv, Job job, String specificQuestion) throws Exception {
        String prompt = buildImprovementPrompt(parsedCv, job, specificQuestion);
        return deepSeekService.chat("improvement_advice", "", prompt);
    }

    // Prompt cho interview questions
    private String buildInterviewPrompt(ParsedCvDto cv, Job job) {
        return """
        Bạn là chuyên gia phỏng vấn kỹ thuật.
        
        === CV CỦA ỨNG VIÊN ===
        - Họ tên: %s
        - Kỹ năng: %s
        - Kinh nghiệm: %s
        
        === MÔ TẢ CÔNG VIỆC ===
        %s
        
        Hãy tạo 5 câu hỏi phỏng vấn phù hợp với ứng viên này, bao gồm:
        1. Câu hỏi về kỹ năng chính trong CV
        2. Câu hỏi về kinh nghiệm thực tế
        3. Câu hỏi tình huống liên quan đến công việc
        4. Câu hỏi về kỹ năng còn thiếu so với JD
        5. Câu hỏi mở để đánh giá tư duy
        
        Trả lời bằng tiếng Việt, mỗi câu hỏi trên 1 dòng.
        """.formatted(
                cv.getPersonal().getFullName(),
                String.join(", ", cv.getSkills()),
                formatExperience(cv),
                aiPrompts.buildJobPrompt(job)
        );
    }

    // Prompt cho improvement advice
    private String buildImprovementPrompt(ParsedCvDto cv, Job job, String specificQuestion) {
        String basePrompt = """
        Bạn là chuyên gia tư vấn CV.
        
        === CV CỦA ỨNG VIÊN ===
        - Họ tên: %s
        - Kỹ năng: %s
        - Kinh nghiệm: %s
        - Kỹ năng còn thiếu so với JD: %s
        
        === MÔ TẢ CÔNG VIỆC ===
        %s
        
        """.formatted(
                cv.getPersonal().getFullName(),
                String.join(", ", cv.getSkills()),
                formatExperience(cv),
                "Dựa trên phân tích match",
                aiPrompts.buildJobPrompt(job)
        );

        if (specificQuestion != null && !specificQuestion.isEmpty()) {
            return basePrompt + "\nCâu hỏi cụ thể: " + specificQuestion + "\n\nHãy trả lời câu hỏi trên một cách chi tiết.";
        }

        return basePrompt + """
        
        Hãy đưa ra 5 gợi ý cụ thể để cải thiện CV:
        1. Về kỹ năng cần bổ sung
        2. Về cách mô tả kinh nghiệm
        3. Về dự án nên thêm
        4. Về chứng chỉ nên có
        5. Về cách trình bày
        
        Trả lời bằng tiếng Việt, ngắn gọn, thực tế.
        """;
    }
}