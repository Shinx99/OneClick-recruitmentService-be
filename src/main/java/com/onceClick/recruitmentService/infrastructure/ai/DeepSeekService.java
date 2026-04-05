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

        return extractContent(resp.getBody());
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