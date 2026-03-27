package com.onceClick.recruitmentService.features.deleteCv;

import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class DeleteCvController {

    private final DeleteCvHandler deleteCvHandler;
    private final CurrentUser currentUser;

    /**
     * Xoá mềm CV theo filename (frontend gửi filename, backend map sang resumeId)
     */
    /*@DeleteMapping("/cv")
    public ApiResponse<String> softDeleteCv(@RequestParam String filename) {
        UUID candidateId = currentUser.getCurrentAccountId();
        deleteCvHandler.softDeleteCvByFilename(candidateId, filename);
        return ApiResponse.success("CV deleted successfully");
    }*/

    /**
     * Xoá mềm tất cả CV của candidate
     */
    /*@DeleteMapping("/cvs")
    public ApiResponse<String> softDeleteAllCvs() {
        UUID candidateId = currentUser.getCurrentAccountId();
        deleteCvHandler.softDeleteAllCvs(candidateId);
        return ApiResponse.success("All CVs deleted successfully");
    }*/

    /**
     * Xoá mềm theo resumeId (nếu cần trong admin / migration script)
     */
    @DeleteMapping("/cv/{resumeId}")
    public ApiResponse<String> softDeleteCvById(@PathVariable UUID resumeId) {
        UUID candidateId = currentUser.getCurrentAccountId();
        deleteCvHandler.softDeleteCv(candidateId, resumeId);
        return ApiResponse.success("CV deleted successfully");
    }
}