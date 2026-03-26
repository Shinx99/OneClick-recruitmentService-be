package com.onceClick.recruitmentService.infrastructure.config;

import com.onceClick.recruitmentService.infrastructure.file.FileTextExtractor;
import com.onceClick.recruitmentService.infrastructure.file.S3FileProcessorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FileConfig {
    @Bean
    @ConditionalOnProperty(name = "spring.file.extractor", havingValue = "s3FileProcessorService")
    public FileTextExtractor fileTextExtractor(S3FileProcessorService s3Service) {
        return s3Service;
    }
}