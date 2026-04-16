package com.onceClick.recruitmentService.features.jobApplication;

import com.onceClick.recruitmentService.features.jobApplication.dto.request.ApplyJobRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.ApplyJobResponse;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.CandidateApplicationResponse;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.JobApplicationResponse;
import com.onceClick.recruitmentService.shared.exception.BadRequestException;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.*;
import com.onceClick.recruitmentService.shared.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;

    /**
     * 1. Ứng tuyển công việc
     */
    public ApplyJobResponse apply(ApplyJobRequest request, UUID candidateId) {
        // Kiểm tra đã ứng tuyển chưa
        boolean alreadyApplied = jobApplicationRepository.existsByJobIdAndCandidateId(
                request.getJobId(), candidateId
        );

        if (alreadyApplied) {
            throw new BadRequestException("Bạn đã ứng tuyển công việc này rồi");
        }

        // Kiểm tra job có tồn tại không
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc"));

        // Tạo application
        JobApplication application = new JobApplication();
        JobApplicationId id = new JobApplicationId();
        id.setJobId(request.getJobId());
        id.setCandidateId(candidateId);
        application.setId(id);
        application.setResumeId(request.getResumeId());
        application.setStatus("pending");
        application.setAppliedAt(Instant.now());
        application.setNote(request.getNote());

        JobApplication saved = jobApplicationRepository.save(application);

        return ApplyJobResponse.builder()
                .jobId(saved.getJobId())
                .candidateId(saved.getCandidateId())
                .status(saved.getStatus())
                .appliedAt(saved.getAppliedAt())
                .build();
    }

    /**
     * 2. Kiểm tra đã ứng tuyển chưa
     */
    public boolean hasApplied(UUID jobId, UUID candidateId) {
        return jobApplicationRepository.existsByJobIdAndCandidateId(jobId, candidateId);
    }

    /**
     * 3. Lấy danh sách job đã ứng tuyển của candidate
     */
    public List<JobApplicationResponse> getMyApplications(UUID candidateId) {
        List<JobApplication> applications = jobApplicationRepository.findByCandidateId(candidateId);

        return applications.stream()
                .map(app -> {
                    Job job = jobRepository.findById(app.getJobId()).orElse(null);
                    return JobApplicationResponse.builder()
                            .jobId(app.getJobId())
                            .jobTitle(job != null ? job.getTitle() : "Unknown")
                            .companyName(job != null ? job.getTitle() : "Unknown")
                            .status(app.getStatus())
                            .appliedAt(app.getAppliedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 4. Lấy danh sách ứng viên của 1 job
     */
    public List<CandidateApplicationResponse> getCandidatesByJob(UUID jobId) {
        List<JobApplication> applications = jobApplicationRepository.findByJobId(jobId);

        return applications.stream()
                .map(app -> CandidateApplicationResponse.builder()
                        .candidateId(app.getCandidateId())
                        .resumeId(app.getResumeId())
                        .status(app.getStatus())
                        .appliedAt(app.getAppliedAt())
                        .note(app.getNote())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 5. Cập nhật trạng thái ứng tuyển
     */
    public void updateStatus(UUID jobId, UUID candidateId, String status, String note) {
        JobApplication application = jobApplicationRepository
                .findByJobIdAndCandidateId(jobId, candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển"));

        application.setStatus(status);
        if (note != null) {
            application.setNote(note);
        }

        jobApplicationRepository.save(application);
        log.info("Updated application status for job {} candidate {} to {}", jobId, candidateId, status);
    }

    /**
     * 6. Hủy ứng tuyển
     */
    public void cancelApplication(UUID jobId, UUID candidateId) {
        JobApplication application = jobApplicationRepository
                .findByJobIdAndCandidateId(jobId, candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển"));

        if (!"pending".equals(application.getStatus())) {
            throw new BadRequestException("Chỉ có thể hủy đơn đang chờ xử lý");
        }

        jobApplicationRepository.delete(application);
        log.info("Cancelled application for job {} by candidate {}", jobId, candidateId);
    }
}