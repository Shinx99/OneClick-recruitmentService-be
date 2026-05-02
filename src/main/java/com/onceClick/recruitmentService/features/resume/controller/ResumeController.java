package com.onceClick.recruitmentService.features.resume.controller;

import com.cloudinary.Api;
import com.onceClick.recruitmentService.features.resume.dto.request.ResumeRequest;
import com.onceClick.recruitmentService.features.resume.dto.response.ResumeResponse;
import com.onceClick.recruitmentService.features.resume.handler.ResumeHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/candidate/profile/resume")
public class ResumeController {

    private final ResumeHandler resumeHandler;
    private final CurrentUser currentUser;


    // -------------------------------------------------------------------------
    // FETCH ALL FOR RESUME CONTROLLER
    // -------------------------------------------------------------------------
    @GetMapping("/fetchAll")
    public ResponseEntity<ApiResponse<PageResponse<ResumeResponse>>> getAllResumes(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ){

        // Step 2: Build Pageable
        Pageable pageable = PageRequest.of(page, size);

        // Step 3: Call service
        ApiResponse<PageResponse<ResumeResponse>> response = resumeHandler.getAllResumes(keyword, pageable);

        // Step 4: Return
        return ResponseEntity.ok(response);
    }



    // -------------------------------------------------------------------------
    // FETCH RESUME CONTROLLER BY RESUME ID
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAnyAuthority('ROLE_candidate', 'ROLE_recruiter')")
    @GetMapping("/fetchResumeById/{resumeId}")
    public ResponseEntity<ApiResponse<ResumeResponse>> fetchResumeByResumeId(@PathVariable UUID resumeId, HttpServletRequest request){
        return ResponseEntity.ok(resumeHandler.fetchResumeDataByResumeId(resumeId, request));
    }




    // -------------------------------------------------------------------------
    // FETCH DATA FOR DEFAULT RESUME CONTROLLER
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @GetMapping("/fetchData")
    public ResponseEntity<ApiResponse<ResumeResponse>> fetchDefaultResume(){

        UUID candidateId = currentUser.getCurrentAccountId();
        ApiResponse<ResumeResponse> response = resumeHandler.fetchResumeDataById(candidateId);
        return ResponseEntity.ok(response);
    }



    // -------------------------------------------------------------------------
    // UPDATE RESUME CONTROLLER
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<ResumeResponse>> updateResume(@RequestBody ResumeRequest request){

        UUID candidateId = currentUser.getCurrentAccountId();
        ApiResponse<ResumeResponse> response = resumeHandler.updateResume(request, candidateId);

        return ResponseEntity.ok(response);
    }



    // -------------------------------------------------------------------------
    // UPDATE AVATAR IMAGE CANDIDATE CONTROLLER
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @PutMapping(value = "/cover/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeResponse>> updateCover(@RequestParam("coverImage")MultipartFile file) throws IOException {

        UUID candidateId = currentUser.getCurrentAccountId();
        ApiResponse<ResumeResponse> response = resumeHandler.updateImg(candidateId, file);
        return ResponseEntity.ok(response);
    }


    // -------------------------------------------------------------------------
    // DELETE AVATAR IMAGE CANDIDATE CONTROLLER
    // -------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ROLE_candidate')")
    @DeleteMapping(value = "/cover/delete")
    public ResponseEntity<ApiResponse<Void>> deleteCover() throws IOException{

        UUID candidateId = currentUser.getCurrentAccountId();
        ApiResponse<Void> response = resumeHandler.deleteImg(candidateId);
        return ResponseEntity.ok(response);
    }

}
