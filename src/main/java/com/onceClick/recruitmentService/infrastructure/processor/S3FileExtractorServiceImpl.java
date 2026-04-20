// infrastructure/processor/S3FileExtractorService.java
package com.onceClick.recruitmentService.infrastructure.processor;

import com.onceClick.recruitmentService.infrastructure.storage.S3StorageService.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Primary
@RequiredArgsConstructor
public class S3FileExtractorServiceImpl implements S3FileExtractorService{

    private final S3StorageService storageService;
    private final FileProcessorService textExtractor;

    @Override
    public String extractTextFromS3(String s3Url) {
        if (s3Url == null || s3Url.isEmpty()) {
            throw new IllegalArgumentException("S3 URL cannot be null");
        }

        try {
            // Parse đúng cấu trúc s3://bucket/candidates/{UUID}/cv/filename
            String path = s3Url.replace("s3://", "");
            String[] parts = path.split("/", 4);  // Cần 4 parts!

            if (parts.length < 4 || !"candidates".equals(parts[1])) {
                throw new IllegalArgumentException("Invalid S3 CV path: " + s3Url);
            }

            String bucket = parts[0];              // recruitment-files
            String accountIdStr = parts[2];        // 949270cd-1fa2-4ad1-9a15-a7d5c10aa755
            String restPath = parts[3];            // cv/CV Fresher.pdf

            // Validate UUID trước khi parse
            if (!isValidUUID(accountIdStr)) {
                throw new IllegalArgumentException("Invalid accountId in S3 path: " + accountIdStr);
            }
            UUID accountId = UUID.fromString(accountIdStr);

            String filename = restPath.substring(restPath.lastIndexOf('/') + 1); // CV Fresher.pdf

            log.info("Parsed s3Url: bucket={}, accountId={}, filename={}",
                    bucket, accountId, filename);

            // Download từ storageService (sẽ dùng key="candidates/{accountId}/cv/{filename}")
            InputStream inputStream = storageService.downloadCvStream(accountId, filename);

            byte[] bytes = inputStreamToBytes(inputStream);
            if (bytes.length > 50 * 1024 * 1024) {
                throw new RuntimeException("File quá lớn: " + bytes.length + " bytes");
            }

            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    filename, filename, getContentType(filename), bytes);

            log.info("S3 file processed: {} ({} bytes)", filename, bytes.length);
            return textExtractor.extractText(multipartFile);

        } catch (Exception e) {
            log.error("Extraction failed for {}: {}", s3Url, e.getMessage(), e);
            throw new RuntimeException("Text extraction failed: " + e.getMessage(), e);
        }
    }

    // Helper method validate UUID
    private boolean isValidUUID(String str) {
        if (str == null || str.length() != 36) return false;
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            inputStream.transferTo(buffer);
            return buffer.toByteArray();
        }
    }

    private String getContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".doc")) return "application/msword";
        return "application/octet-stream";
    }
}