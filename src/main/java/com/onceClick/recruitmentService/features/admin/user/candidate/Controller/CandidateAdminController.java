package com.onceClick.recruitmentService.features.admin.user.candidate.Controller;

import com.onceClick.recruitmentService.features.admin.user.candidate.DTO.CandidateDetailResponse;
import com.onceClick.recruitmentService.features.admin.user.candidate.DTO.CandidateListResponse;
import com.onceClick.recruitmentService.features.admin.user.candidate.Handler.CandidateAdminHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users/candidates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_admin')")
public class CandidateAdminController {
    private final CandidateAdminHandler candidateAdminHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CandidateListResponse>>> getCandidates(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<CandidateListResponse> page = candidateAdminHandler.getCandidates(status, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/{candidateId}")
    public ResponseEntity<ApiResponse<CandidateDetailResponse>> getCandidateDetail(@PathVariable UUID candidateId) {
        CandidateDetailResponse detail = candidateAdminHandler.getCandidateDetail(candidateId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @PutMapping("/{candidateId}/status")
    public ResponseEntity<ApiResponse<Void>> updateCandidateStatus(
            @PathVariable UUID candidateId,
            @RequestParam String status) {
        candidateAdminHandler.updateCandidateStatus(candidateId, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
