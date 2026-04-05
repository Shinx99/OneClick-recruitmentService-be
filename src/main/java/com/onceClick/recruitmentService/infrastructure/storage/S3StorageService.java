// infrastructure/storage/FileStorageService.java
package com.onceClick.recruitmentService.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

public interface S3StorageService {

    String uploadCv(MultipartFile file, UUID candidateId) throws IOException;
    InputStream downloadCvStream(UUID accountId, String filename);
    List<S3Object> listCvObjects(UUID accountId);
    String getContentType(String filename);
    long getFileSize(UUID accountId, String filename);
    byte[] downloadCvBytes(UUID accountId, String filename);
    void deleteObject(String objectKey);


    String uploadTempFile(String key, MultipartFile file) throws IOException;
    void deleteTempObject(String key);


}