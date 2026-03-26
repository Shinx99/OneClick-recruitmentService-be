package com.onceClick.recruitmentService.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;
import java.util.Map;

@Slf4j
@Service("deepseekAiService")  // Named bean
@RequiredArgsConstructor
public class DeepSeekService implements AiService {
    
    private final AiConfig config;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${deepseek.api.url:https://api.deepseek.com}")
    private String apiUrl;

    /*@Value("${deepseek.api.key:sk-test}")
    private String apiKey;*/

    @Value("${deepseek.api.endpoint:/v1/chat/completions}")
    private String apiEndpoint;


    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public String chat(String task, String prompt) throws Exception {
        log.info("DeepSeek AI call: {}@{} tokens~{}", task, config.getModel(), prompt.length()/4);

        Map<String, Object> body = Map.of(
            "model", config.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", getSystemPrompt(task)),
                Map.of("role", "user", "content", prompt)
            ),
            "max_tokens", config.getMaxTokens(),
            "temperature", config.getTemperature()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + config.getApiKey());
        headers.set("Accept", "application/json");

        HttpEntity<Map> req = new HttpEntity<>(body, headers);

        log.info("Gọi DeepSeek API với URL: {}", apiUrl + "/chat/completions");
        log.info("API Key (first 10 chars): {}...", config.getApiKey().substring(0, Math.min(10, config.getApiKey().length())));

        ResponseEntity<String> resp = restTemplate.postForEntity(
                config.getBaseUrl() + apiEndpoint,
                req,
                String.class
        );

        log.info("DeepSeek API Response Status: {}", resp.getStatusCode());

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("AI API error: " + resp.getStatusCode());
        }

        return extractContent(resp.getBody());
    }

    @Override
    public String parseCvText(String cvText) throws Exception {
        return chat("scan_cv", cvText);
    }

    @Override
    public String matchCvJob(String cvText, String jobDesc) throws Exception {
        String prompt = "Match CV vs Job desc → % similarity + matched skills\nCV: " + cvText + "\nJob: " + jobDesc;
        return chat("ai_matching", prompt);
    }

    @Override
    public <T> T parseResponse(String jsonResponse, Class<T> clazz) throws Exception {
        return objectMapper.readValue(jsonResponse, clazz);
    }

    private String getSystemPrompt(String task) {
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

    private String extractContent(String response) {
        try {
            JsonNode node = objectMapper.readTree(response);
            return node.path("choices").path(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("AI response parse failed: {}", response);
            throw new RuntimeException("Invalid AI response format", e);
        }
    }
}