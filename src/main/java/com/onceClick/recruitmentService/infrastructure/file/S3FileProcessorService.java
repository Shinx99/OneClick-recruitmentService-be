package com.onceClick.recruitmentService.infrastructure.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class S3FileProcessorService implements FileTextExtractor{
    private final S3Client s3Client;
    private final FileProcessorService fileProcessor;

    @Value("${aws.s3.bucket:recruitment-files}")
    private String bucketName;

    @Override
    public String extractText(MultipartFile file) throws Exception {
        throw new UnsupportedOperationException("Use extractTextFromS3 for S3 files");
    }

    @Override
    public String extractTextFromS3(String s3Url) {

        if (s3Url == null || s3Url.isEmpty()) {
            throw new IllegalArgumentException("S3 URL cannot be null");
        }

        try {
            String[] parts = s3Url.replace("s3://", "").split("/", 2);
            String bucket = parts[0];
            String key = parts[1];

            GetObjectRequest req = GetObjectRequest.builder()
                    .bucket(bucket).key(key).build();

            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(req)) {
                byte[] bytes = s3Object.readAllBytes();

                if (bytes.length > 50 * 1024 * 1024) {  // 50MB limit
                    throw new RuntimeException("File too large: " + bytes.length);
                }

                String filename = key.substring(key.lastIndexOf('/') + 1);

                // ✅ FIX: Dùng ByteArrayMultipartFile thay MockMultipartFile
                MultipartFile multipartFile = new ByteArrayMultipartFile(
                        filename, filename, "application/pdf", bytes);

                log.info("S3 file processed: {} ({} bytes)", filename, bytes.length);
                return fileProcessor.extractText(multipartFile);
            }

        } catch (IOException e) {
            log.error("S3 extract failed for {}: {}", s3Url, e.getMessage());
            throw new RuntimeException("File extract failed from S3: " + s3Url, e);
        } catch (Exception e) {
            log.error("Processor failed for {}: {}", s3Url, e.getMessage());
            throw new RuntimeException("Text extraction failed: " + e.getMessage(), e);
        }
    }

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

    // S3FileProcessorService.java – thêm method
    public ResponseEntity<Resource> downloadCv(UUID accountId, String filename) {
        String key = "candidates/" + accountId + "/cv/" + filename;

        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(req);

        Resource resource = new InputStreamResource(s3Object);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private String getBucketName() {
        // @Value("${aws.s3.bucket}")
        return bucketName;
    }

    @Override
    public boolean isSupportedFile(String fileName) {
        return fileProcessor.isSupportedFile(fileName);
    }

    @Override
    public String getFileType(String fileName) {
        return fileProcessor.getFileType(fileName);
    }
}