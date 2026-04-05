package com.onceClick.recruitmentService.infrastructure.ai.prompt;

import java.util.List;
import java.util.stream.Collectors;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatMessageDto;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AiPromptsImpl implements AiPrompts {


    @Override
    public String getSystemPromptForScanCV(String task) {
        return switch (task) {
            case "scan_cv" -> """
                       Parse CV tiếng Việt → EXACT JSON format theo DTO:
                    {
                      "personal": {
                        "fullName": "Họ tên đầy đủ",
                        "email": "email@example.com", 
                        "phoneNumber": "0123456789",
                        "location": "HCM/HN",
                        "avatarUrl": null,
                        "linkedinUrl": null
                      },
                      "workExperience": [
                        {
                          "company": "Công ty ABC",
                          "position": "Backend Developer",
                          "startDate": "2023-05-01",  // YYYY-MM-DD format
                          "endDate": "2024-12-31",
                          "description": "Mô tả ngắn"
                        }
                      ],
                      "education": [
                        {
                          "schoolName": "ĐH FPT",
                          "degree": "Cao đẳng CNTT",
                          "fieldOfStudy": "Phát triển phần mềm",
                          "startDate": "2022-09",
                          "endDate": "2025-12",
                          "isCurrent": true
                        }
                      ],
                      "certificates": [],
                      "skills": ["Java", "Spring Boot", "MySQL"],
                      "extractedFields": {
                        "careerGoal": "Trở thành Senior Backend",
                        "major": "Phát triển phần mềm", 
                        "experienceYear": 1.5,
                        "salaryExpectation": "15-20tr",
                        "totalExperienceMonths": 18,
                        "topSkills": ["Java", "Spring", "Docker"]
                      }
                    }
                    ALWAYS return ALL fields, null/empty nếu không có.
                    """;
            case "ai_matching" -> "Match CV vs Job → return % similarity + matched skills";
            default -> "You are helpful recruitment AI assistant";
        };
    }

    /**
     * Tạo job description chi tiết cho AI match + góp ý
     */
    @Override
    public String buildJobPrompt(Job job) {
        return """
            Job Title: %s
            Company: %s
            Location: %s
            
            MÔ TẢ CÔNG VIỆC:
            %s
            
            YÊU CẦU:
            %s
            
            Ưu tiên: %s
            Level: %s
            Kinh nghiệm: %s năm
            Mức lương: %s - %s
            Hạn nộp: %s
            
            """.formatted(
                job.getTitle(),
                "Công ty ABC", // TODO: join với Company entity
                job.getProvince() + (job.getCommune() != null ? ", " + job.getCommune() : ""),
                job.getDescription() != null ? job.getDescription() : "",
                job.getRequirement() != null ? job.getRequirement() : "",
                job.getMajorPreferred() != null ? job.getMajorPreferred() : "Không yêu cầu",
                job.getLevel() != null ? job.getLevel() : "Junior",
                job.getExperienceMinYear() != null ? job.getExperienceMinYear() : "0",
                formatSalary(job.getSalaryMin()),
                formatSalary(job.getSalaryMax()),
                job.getApplicationDeadline() != null ? job.getApplicationDeadline() : "Không giới hạn"
        );
    }

    @Override
    public String buildCvJobMatchPrompt(String cvParsedJson, String jobPrompt) {
        return """
            Phân tích CV vs Job Description. Trả JSON EXACT format:
            
            {
              "similarity": 85.0,
              "matchedSkills": ["Java", "Spring Boot"],
              "missingSkills": ["Docker", "Kubernetes"],
              "matchReason": "Phù hợp 85%...",
              "improvementTips": [
                "Thêm kinh nghiệm Docker",
                "Cập nhật salary expectation 20-30tr",
                "Bổ sung projects GitHub"
              ]
            }
            
            CV JSON: %s
            
            JOB: %s
            """.formatted(cvParsedJson, jobPrompt);
    }

    private String formatSalary(BigDecimal salary) {
        if (salary == null) return "Thương lượng";
        return salary.divide(BigDecimal.valueOf(1000000)).setScale(0) + "tr";
    }


    @Override
    public String getSystemPromptForChat() {
        return """
            Bạn là trợ lý AI của nền tảng OnceClick.

            Nhiệm vụ của bạn:
            - hướng dẫn người dùng sử dụng website OnceClick
            - hỗ trợ các tính năng: tài khoản, đăng nhập, bảo mật, profile
            - hỗ trợ CV, job, ứng tuyển, hồ sơ công ty
            - hỗ trợ social, tin nhắn, thông báo, hướng dẫn thao tác tính năng

            Quy tắc trả lời:
            - trả lời ngắn gọn, rõ ràng, dễ hiểu, theo từng bước nếu là hướng dẫn thao tác
            - chỉ trả lời trong phạm vi hệ thống OnceClick
            - nếu câu hỏi ngoài phạm vi website, từ chối lịch sự và nói rõ chỉ hỗ trợ tính năng trên OnceClick
            - nếu người dùng yêu cầu gặp admin, người thật, kỹ thuật viên, chăm sóc khách hàng, hoặc cần hỗ trợ thủ công, hãy trả lời ngắn gọn theo hướng xác nhận chuyển admin
            - không tự bịa thông tin hệ thống không có
            - ưu tiên trả lời bằng tiếng Việt
            """;
    }

    @Override
    public String getOutOfScopePrompt() {
        return """
            Em chỉ hỗ trợ các vấn đề trong hệ thống OnceClick như tài khoản, CV,
            job, ứng tuyển, hồ sơ doanh nghiệp và các tính năng trên website.
            Các nội dung ngoài phạm vi này, em chưa thể tư vấn chuyên sâu.
            """;
    }


    @Override
    public String buildPromptForChat(
            String userMessage,
            List<AiChatMessageDto> history,
            String featureContext) {

        String historyStr = history == null ? "" : history.stream()
                .limit(10)
                .map(m -> "[" + m.getSenderType() + "] " + m.getContent())
                .collect(Collectors.joining("\n"));

        return """
                Context (system):
                %s

                Feature context:
                %s

                Lịch sử cuộc trò chuyện (tối đa 10 tin gần nhất):
                %s

                User hiện tại hỏi:
                %s

                Yêu cầu trả lời:
                - Nếu user cần hướng dẫn thao tác trên web, hãy hướng dẫn từng bước ngắn gọn.
                - Nếu user yêu cầu gặp admin/người thật, hãy trả lời xác nhận sẽ chuyển hỗ trợ admin.
                - Nếu câu hỏi ngoài phạm vi hệ thống OnceClick, hãy từ chối lịch sự.
                """.formatted(
                getSystemPromptForChat(),
                featureContext != null ? featureContext : "Hỗ trợ sử dụng website OnceClick",
                historyStr,
                userMessage
        );
    }
}
