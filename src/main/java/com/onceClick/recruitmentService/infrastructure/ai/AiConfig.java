package com.onceClick.recruitmentService.infrastructure.ai;
// infrastructure/ai/DeepSeekConfig.java

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "deepseek.api")
@Data
@Component
public class AiConfig {
    private Provider provider = Provider.DEEPSEEK;  // Enum: DEEPSEEK, OPENAI, OLLAMA

    @Value("${deepseek.api.key:sk-test}")
    private String apiKey;

    private String baseUrl = "https://api.deepseek.com";
    private String model = "deepseek-chat";
    private int maxTokens = 4000;
    private double temperature = 0.1;

    public enum Provider {
        DEEPSEEK, OPENAI, ANTHROPIC, OLLAMA
    }
}