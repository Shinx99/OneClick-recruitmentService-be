package com.onceClick.recruitmentService.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.access-key-id:minioadmin}")
    private String accessKey;

    @Value("${aws.secret-access-key:minioadmin}")
    private String secretKey;

    @Value("${aws.s3.endpoint:http://localhost:9000}")
    private String endpoint;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(endpoint))
                .forcePathStyle(true)  // MinIO!
                .build();
    }
}

/**
1. Candidate upload CV → POST /api/resumes/upload
   ↓ S3Service.upload() → "s3://resumes/uuid-cv.pdf"
        ↓ resume_repo.save(resume_upload_url)

2. Click "Scan CV" → POST /api/ai/cv/scan/resume-uuid
   ↓ CvAiHandler.scan()
   ↓ S3FileProcessorService.extractFromS3("s3://...")
   ↓ FileProcessorService.extractText() → "Nguyễn Văn A, Java..."
        ↓ AiService.chat("scan_cv", text) → DeepSeek
   ↓ parsed_data = {"skills":["Java"]} → Save DB*/
