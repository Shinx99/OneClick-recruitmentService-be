package com.onceClick.recruitmentService.infrastructure.config;

import com.onceClick.recruitmentService.infrastructure.processor.*;
import com.onceClick.recruitmentService.infrastructure.storage.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class FileConfig {

    // 1. S3StorageService (OK)
    @Bean @Primary
    public S3StorageService s3StorageService(S3Client s3Client) {
        return new S3StorageServiceImpl(s3Client);
    }

    // 2. THÊM FileProcessorService bean!
    @Bean @Primary
    public FileProcessorService fileProcessorService() {
        return new FileProcessorServiceImpl();
    }

    // 3. S3FileExtractorService (sẽ work)
    @Bean @Primary
    public S3FileExtractorService s3FileExtractorService(
            S3StorageService storageService,
            FileProcessorService textExtractor) {  // ← Bây giờ có bean!
        return new S3FileExtractorServiceImpl(storageService, textExtractor);
    }
}