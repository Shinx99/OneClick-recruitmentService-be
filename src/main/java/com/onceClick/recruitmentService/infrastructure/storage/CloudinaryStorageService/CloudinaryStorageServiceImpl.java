package com.onceClick.recruitmentService.infrastructure.storage.CloudinaryStorageService;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.netty.util.internal.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryStorageServiceImpl implements CloudinaryStorageService {

    private final Cloudinary cloudinary;


    @Override
    public String uploadImage(MultipartFile file, String folder) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "image",
                        "overwrite", true
                )
        );
        return (String) uploadResult.get("secure_url");
    }


    @Override
    public void deleteImage(String publicId) throws IOException {
        if(publicId == null || publicId.isBlank()) {
            log.warn("deleteImage called with null/blank publicId, skipping.");
            return;
        }

        Map result = cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("invalidate", true)
        );

        String resultStatus = (String) result.get("result");
        if(!"ok".equals(resultStatus)){
            log.warn("Cloudinary deleteImage failed for publicId '{}': {}", publicId, resultStatus);
        } else {
            log.info("Cloudinary deleteImage success for publicId '{}'", publicId);
        }
    }
}
