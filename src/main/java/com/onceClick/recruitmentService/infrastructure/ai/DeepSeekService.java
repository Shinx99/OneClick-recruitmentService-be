package com.onceClick.recruitmentService.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onceClick.recruitmentService.infrastructure.ai.prompt.AiPrompts;
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
    private final AiPrompts aiPrompts;

    @Value("${deepseek.api.url:https://api.deepseek.com}")
    private String apiUrl;

    /*@Value("${deepseek.api.key:sk-test}")
    private String apiKey;*/

    @Value("${deepseek.api.endpoint:/v1/chat/completions}")
    private String apiEndpoint;

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public String chat(String task, String systemPrompt, String userPrompt) throws Exception {
        log.info("DeepSeek AI call: {}@{} system~{} tokens~{}", task, config.getModel(), systemPrompt.length()/4, userPrompt.length()/4);

        Map<String, Object> body = Map.of(
                "model", config.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
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



        String content = extractContent(resp.getBody());

        // Nếu là task cần JSON (ai_matching hoặc scan_cv), clean response
        if ("ai_matching".equals(task) || "scan_cv".equals(task)) {
            content = extractAndCleanJson(content);
        }

        return content;
    }


    @Override
    public String chat(String task, String prompt) throws Exception {
        return chat(task, aiPrompts.getSystemPromptForScanCV(task), prompt);
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



    private String extractContent(String response) {
        try {
            JsonNode node = objectMapper.readTree(response);
            return node.path("choices").path(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("AI response parse failed: {}", response);
            throw new RuntimeException("Invalid AI response format", e);
        }
    }

    // DeepSeekService.java - THÊM METHOD NÀY
    private String extractAndCleanJson(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new RuntimeException("Empty response from AI");
        }

        String cleaned = response.trim();

        // Log raw response để debug
        log.info("Raw response before cleaning (first 500 chars): {}",
                cleaned.length() > 500 ? cleaned.substring(0, 500) : cleaned);

        // Loại bỏ markdown code blocks
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
            log.info("Removed ```json prefix");
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
            log.info("Removed ``` prefix");
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
            log.info("Removed ``` suffix");
        }

        cleaned = cleaned.trim();

        // Tìm vị trí bắt đầu của JSON
        int startBrace = cleaned.indexOf('{');
        int startBracket = cleaned.indexOf('[');
        int startIndex = -1;

        if (startBrace >= 0 && startBracket >= 0) {
            startIndex = Math.min(startBrace, startBracket);
        } else if (startBrace >= 0) {
            startIndex = startBrace;
        } else if (startBracket >= 0) {
            startIndex = startBracket;
        }

        if (startIndex == -1) {
            log.error("No JSON found in AI response: {}", response);
            throw new RuntimeException("No JSON found in AI response");
        }

        // Tìm vị trí kết thúc của JSON
        int endIndex = cleaned.length() - 1;
        int braceCount = 0;
        int bracketCount = 0;
        boolean inString = false;
        boolean escaped = false;
        char startChar = cleaned.charAt(startIndex);

        for (int i = startIndex; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"' && !escaped) {
                inString = !inString;
                continue;
            }

            if (!inString) {
                if (c == '{') braceCount++;
                if (c == '}') {
                    braceCount--;
                    if (startChar == '{' && braceCount == 0) {
                        endIndex = i;
                        break;
                    }
                }
                if (c == '[') bracketCount++;
                if (c == ']') {
                    bracketCount--;
                    if (startChar == '[' && bracketCount == 0) {
                        endIndex = i;
                        break;
                    }
                }
            }
        }

        String jsonContent = cleaned.substring(startIndex, endIndex + 1);

        log.info("Extracted JSON (first 500 chars): {}",
                jsonContent.length() > 500 ? jsonContent.substring(0, 500) : jsonContent);
        // Validate JSON
        try {
            objectMapper.readTree(jsonContent);
            return jsonContent;
        } catch (Exception e) {
            log.error("Invalid JSON after cleaning: {}", jsonContent);
            throw new RuntimeException("Invalid JSON response from AI: " + e.getMessage(), e);
        }
    }
}






 /*@Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public String chat(String task, String prompt) throws Exception {
        log.info("DeepSeek AI call: {}@{} tokens~{}", task, config.getModel(), prompt.length()/4);

        Map<String, Object> body = Map.of(
            "model", config.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", aiPrompts.getSystemPromptForScanCV(task)),
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
    }*/