package com.onceClick.recruitmentService.infrastructure.storage.CloudinaryStorageService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryStorageService {

    String uploadImage(MultipartFile file, String folder) throws IOException;

    default void deleteImage(String publicId) throws IOException {} ;
}
