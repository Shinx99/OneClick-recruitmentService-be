package com.onceClick.recruitmentService.infrastructure.processor;
// infrastructure/processor/S3TextExtractorService.java

public interface S3FileExtractorService {
    String extractTextFromS3(String s3Url) throws Exception;

}
