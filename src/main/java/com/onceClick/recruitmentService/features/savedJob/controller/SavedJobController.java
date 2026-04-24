package com.onceClick.recruitmentService.features.savedJob.controller;


import com.onceClick.recruitmentService.features.savedJob.dto.response.SavedJobResponseDto;
import com.onceClick.recruitmentService.features.savedJob.dto.response.SavedJobStatusDto;
import com.onceClick.recruitmentService.features.savedJob.handler.CheckSavedJobHandler;
import com.onceClick.recruitmentService.features.savedJob.handler.GetSavedJobsHandler;
import com.onceClick.recruitmentService.features.savedJob.handler.SaveJobHandler;
import com.onceClick.recruitmentService.features.savedJob.handler.UnsaveJobHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/saved-job")
@PreAuthorize(value = "hasAuthority('ROLE_candidate')")
public class SavedJobController {

    private final SaveJobHandler saveJobHandler;
    private final UnsaveJobHandler unsaveJobHandler;
    private final CheckSavedJobHandler checkSavedJobHandler;
    private final GetSavedJobsHandler getSavedJobsHandler;
    private final CurrentUser currentUser;

    @PostMapping("/{jobId}")
    public ResponseEntity<ApiResponse<SavedJobStatusDto>> getSavedJobStatus(@PathVariable UUID jobId){
        UUID candidateId = currentUser.getCurrentAccountId();
        return ResponseEntity.ok(saveJobHandler.saveJob(candidateId, jobId));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<SavedJobStatusDto>> unsaveJob(
            @PathVariable UUID jobId
    ) {
        UUID candidateId = currentUser.getCurrentAccountId();
        return ResponseEntity.ok(unsaveJobHandler.unsaveJob(candidateId, jobId));
    }

    @GetMapping("/{jobId}/check")
    public ResponseEntity<ApiResponse<SavedJobStatusDto>> checkSaved(
            @PathVariable UUID jobId
    ) {
        UUID candidateId = currentUser.getCurrentAccountId();
        return ResponseEntity.ok(checkSavedJobHandler.isSaved(candidateId, jobId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SavedJobResponseDto>>> getSavedJobs(
            Pageable pageable
    ) {
        UUID candidateId = currentUser.getCurrentAccountId();
        return ResponseEntity.ok(getSavedJobsHandler.getSavedJobs(candidateId, pageable));
    }
}
