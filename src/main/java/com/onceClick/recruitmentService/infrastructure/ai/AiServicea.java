/*
// infrastructure/ai/DeepSeekService.java
package com.onceClick.recruitmentService.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServicea {
    private final AiConfig config;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public String chat(String task, String prompt) {

        log.info("AI call: {}@{} tokens~{}", task, config.getModel(), prompt.length()/4);

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
        headers.setBearerAuth(config.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map> req = new HttpEntity<>(body, headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(
                config.getBaseUrl() + "/v1/chat/completions", req, String.class);

        return extractContent(resp.getBody());
    }

    private String getSystemPrompt(String task) {
        return switch (task) {
            case "scan_cv" -> "Parse CV tiếng Việt → JSON skills/exp/edu";
            case "ai_matching" -> "Match CV vs Job → % + matched skills";
            default -> "You are helpful AI assistant";
        };
    }

    private String extractContent(String response) {
        try {
            JsonNode node = objectMapper.readTree(response);
            return node.get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Invalid AI response", e);
        }
    }

    private int estimateTokens(String text) {
        return Math.max(1, text.length() / 4);  // Rough estimate
    }



}*/
