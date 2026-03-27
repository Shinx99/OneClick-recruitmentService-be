package com.onceClick.recruitmentService.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

// infrastructure/storage/S3StorageService.java
@Slf4j
@Primary
@RequiredArgsConstructor
public class S3StorageServiceImpl implements S3StorageService {
    
    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket:recruitment-files}")
    private String bucketName;
    
    @Override
    public String uploadCv(MultipartFile file, UUID candidateId) throws IOException {
        String key = "candidates/" + candidateId + "/cv/" + file.getOriginalFilename();
        String bucket = getBucketName();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try (InputStream inputStream = file.getInputStream()) {  // ← IOException here
            s3Client.putObject(request,
                    RequestBody.fromInputStream(inputStream, file.getSize()));
        }

        return "s3://" + bucket + "/" + key;
    }
    
    @Override
    public InputStream downloadCvStream(UUID accountId, String filename) {
        String key = "candidates/" + accountId + "/cv/" + filename;

        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            return s3Client.getObject(req);
        } catch (NoSuchKeyException e) {
            log.warn("CV not found: {}", key);
            throw new RuntimeException("File not found: " + filename, e);
        }
    }
    
    @Override
    public List<S3Object> listCvObjects(UUID accountId) {
        String prefix = "candidates/" + accountId + "/cv/";

        ListObjectsV2Request req = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        return s3Client.listObjectsV2(req).contents();
    }

    private String getBucketName() {
        // @Value("${aws.s3.bucket}")
        return bucketName;
    }


    // 1. Dynamic Content-Type
    @Override
    public String getContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".doc")) return "application/msword";
        return "application/octet-stream";
    }

    // 2. Get file size (cho Content-Length header)
    @Override
    public long getFileSize(UUID accountId, String filename) {
        String key = "candidates/" + accountId + "/cv/" + filename;
        HeadObjectRequest req = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        HeadObjectResponse response = s3Client.headObject(req);
        return response.contentLength();
    }

    @Override
    public byte[] downloadCvBytes(UUID accountId, String filename) {
        String key = "candidates/" + accountId + "/cv/" + filename;

        log.info("Download bytes: key={}", key);

        try {
            GetObjectRequest req = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return s3Client.getObjectAsBytes(req).asByteArray();

        } catch (NoSuchKeyException e) {
            log.warn("File not found: {}", key);
            throw new RuntimeException("File không tồn tại: " + filename, e);
        }
    }

}