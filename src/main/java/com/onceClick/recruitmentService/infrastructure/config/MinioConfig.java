package com.onceClick.recruitmentService.infrastructure.config;


import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${spring.aws.s3.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${spring.aws.access-key-id:minioadmin}")
    private String accessKey;

    @Value("${spring.aws.secret-access-key:minioadmin}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }


}
