package com.onceClick.recruitmentService.infrastructure.processor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

// infrastructure/file/FileProcessingConfig.java
@ConfigurationProperties(prefix = "app.file.process")
@Data
@Component
public class FileProcessingConfig {
    private List<String> supportedTypes = List.of("pdf", "docx", "txt");
    private long maxSizeMb = 10;
    private boolean ocrEnabled = false;  // Tesseract future
    private String defaultCharset = "UTF-8";
}