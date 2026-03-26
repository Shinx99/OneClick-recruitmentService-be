package com.onceClick.recruitmentService.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileTextExtractor {
    String extractText(MultipartFile file) throws Exception;
    String extractTextFromS3(String s3Url) throws Exception;  // S3 specific
    boolean isSupportedFile(String fileName);
    String getFileType(String fileName);
}