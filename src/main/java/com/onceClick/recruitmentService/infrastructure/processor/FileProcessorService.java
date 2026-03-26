package com.onceClick.recruitmentService.infrastructure.processor;

import org.springframework.web.multipart.MultipartFile;

public interface FileProcessorService {
    String extractText(MultipartFile file) throws Exception;
    boolean isSupportedFile(String fileName);
    String getFileType(String fileName);
}