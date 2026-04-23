package com.onceClick.recruitmentService.features.education.Controller;

import com.onceClick.recruitmentService.features.education.DTO.EducationRequestDto;
import com.onceClick.recruitmentService.features.education.DTO.EducationResponseDto;
import com.onceClick.recruitmentService.features.education.Handler.EducationHandler;
import com.onceClick.recruitmentService.infrastructure.storage.CloudinaryStorageService.CloudinaryStorageService;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recruitment/candidate/education")
@RequiredArgsConstructor
public class CandidateEducationController {
    private final EducationHandler educationHandler;
    private final CurrentUser currentUser;
    private final CloudinaryStorageService cloudinaryStorageService;

    @GetMapping
    public ApiResponse<List<EducationResponseDto>> getAll() {
        UUID candidateId = currentUser.getCurrentAccountId();
        return ApiResponse.success(educationHandler.getEducations(candidateId));
    }

    @PostMapping
    public ApiResponse<EducationResponseDto> create(@RequestBody EducationRequestDto request) {
        UUID candidateId = currentUser.getCurrentAccountId();
        return ApiResponse.success(educationHandler.addEducation(candidateId, request));
    }

    @PutMapping("/{educationId}")
    public ApiResponse<EducationResponseDto> update(@PathVariable UUID educationId,
                                                    @RequestBody EducationRequestDto request) {
        UUID candidateId = currentUser.getCurrentAccountId();
        return ApiResponse.success(educationHandler.updateEducation(candidateId, educationId, request));
    }

    @DeleteMapping("/{educationId}")
    public ApiResponse<Void> delete(@PathVariable UUID educationId) {
        UUID candidateId = currentUser.getCurrentAccountId();
        educationHandler.deleteEducation(candidateId, educationId);
        return ApiResponse.success(null);
    }

    @PostMapping(value = "/{educationId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadEducationImage(@PathVariable UUID educationId,
                                                    @RequestParam("image") MultipartFile file) throws IOException {
        UUID candidateId = currentUser.getCurrentAccountId();
        String imageUrl = educationHandler.uploadEducationImage(educationId, candidateId, file);
        return ApiResponse.success(imageUrl);
    }

    @DeleteMapping("/{educationId}/image")
    public ApiResponse<Void> deleteEducationImage(@PathVariable UUID educationId) throws IOException {
        UUID candidateId = currentUser.getCurrentAccountId();
        educationHandler.deleteEducationImage(educationId, candidateId);
        return ApiResponse.success(null);
    }

    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadTempEducationImage(@RequestParam("image") MultipartFile file) throws IOException {
        UUID candidateId = currentUser.getCurrentAccountId(); // Để đảm bảo đã đăng nhập
        String imageUrl = cloudinaryStorageService.uploadImage(file, "educations/certificates");
        return ApiResponse.success(imageUrl);
    }
}
