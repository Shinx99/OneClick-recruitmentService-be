package com.onceClick.recruitmentService.features.jobApplication.service;

import com.onceClick.recruitmentService.features.jobApplication.dto.request.ApplicationFilterRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.request.InterviewScheduleRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.request.UpdateStatusRequest;
import com.onceClick.recruitmentService.features.jobApplication.dto.response.*;
import com.onceClick.recruitmentService.shared.constant.ApplicationConstants;
import com.onceClick.recruitmentService.shared.exception.BadRequestException;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.exception.UnauthorizedException;
import com.onceClick.recruitmentService.shared.persistence.entity.*;
import com.onceClick.recruitmentService.shared.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.onceClick.recruitmentService.shared.constant.ApplicationConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruiterApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final InterviewScheduleRepository interviewScheduleRepository;
    private final NotificationService notificationService;

    /**
     * 1. Lấy danh sách ứng viên theo job (có phân trang, lọc)
     */
    @Transactional(readOnly = true)
    public Page<ApplicationListResponse> getApplications(UUID jobId, ApplicationFilterRequest filter, UUID employerId) {
        verifyJobAccess(jobId, employerId);

        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDir()), filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Page<JobApplication> applications;
        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            // S"WithPagination" ở cuối
            applications = jobApplicationRepository.findByJobIdAndStatus(jobId, filter.getStatus(), pageable);
        } else {
            // bỏ "WithPagination" ở cuối
            applications = jobApplicationRepository.findByJobId(jobId, pageable);
        }

        // Lấy thông tin job để có title
        Job job = jobRepository.findById(jobId).orElse(null);
        final String jobTitle = job != null ? job.getTitle() : "Đã ứng tuyển";


        return applications.map(app -> {
            Candidate candidate = candidateRepository.findById(app.getCandidateId()).orElse(null);
            Resume resume = resumeRepository.findById(app.getResumeId()).orElse(null);

            // Lấy kinh nghiệm từ resume (nếu có)
            BigDecimal experienceYear = null;
            if (resume != null) {
                experienceYear = resume.getExperienceYear();
            }

            return ApplicationListResponse.builder()
                    .applicationId(app.getApplicationId())
                    .jobId(jobId)
                    .jobTitle(jobTitle)
                    .candidateId(app.getCandidateId())
                    .candidateName(candidate != null ? candidate.getName() : "Unknown")
                    .candidateEmail(candidate != null ? candidate.getEmail() : "Unknown")
                    .candidatePhone(candidate != null ? candidate.getPhone() : null)
                    .resumeUrl(convertS3UriToMinioUrl(resume.getResumeUploadUrl()))
                    .status(app.getStatus())
                    .statusDisplay(getStatusDisplay(app.getStatus()))
                    .appliedAt(app.getAppliedAt())
                    .matchScore(app.getMatchScore())
                    .hasInterviewScheduled(hasUpcomingInterview(app.getApplicationId()))
                    .candidateExperienceYear(experienceYear)
                    .build();
        });
    }

    /**
     * 2. Thống kê số lượng ứng viên theo trạng thái
     */
    @Transactional(readOnly = true)
    public ApplicationStatsResponse getApplicationStats(UUID jobId, UUID employerId) {
        verifyJobAccess(jobId, employerId);

        List<Object[]> stats = jobApplicationRepository.countByStatusForJob(jobId);
        
        long total = 0;
        long pending = 0, reviewed = 0, interview = 0, accepted = 0, rejected = 0;
        
        for (Object[] row : stats) {
            String status = (String) row[0];
            long count = (long) row[1];
            total += count;
            
            switch (status) {
                case STATUS_PENDING -> pending = count;
                case STATUS_REVIEWED -> reviewed = count;
                case STATUS_INTERVIEW -> interview = count;
                case STATUS_ACCEPTED -> accepted = count;
                case STATUS_REJECTED -> rejected = count;
            }
        }
        
        return ApplicationStatsResponse.builder()
                .total(total)
                .pending(pending)
                .reviewed(reviewed)
                .interview(interview)
                .accepted(accepted)
                .rejected(rejected)
                .pendingRate(total > 0 ? (double) pending / total * 100 : 0)
                .reviewedRate(total > 0 ? (double) reviewed / total * 100 : 0)
                .interviewRate(total > 0 ? (double) interview / total * 100 : 0)
                .acceptedRate(total > 0 ? (double) accepted / total * 100 : 0)
                .rejectedRate(total > 0 ? (double) rejected / total * 100 : 0)
                .build();
    }

    /**
     * 3. Chi tiết đơn ứng tuyển
     */
    @Transactional(readOnly = true)
    public ApplicationDetailResponse getApplicationDetail(UUID applicationId, UUID employerId) {
        JobApplication application = getApplicationWithPermission(applicationId, employerId);
        
        Job job = jobRepository.findById(application.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc"));
        
        Candidate candidate = candidateRepository.findById(application.getCandidateId()).orElse(null);
        Resume resume = resumeRepository.findById(application.getResumeId()).orElse(null);
        
        List<StatusHistoryResponse> history = getApplicationHistory(applicationId, employerId);
        InterviewScheduleResponse upcomingInterview = getUpcomingInterview(applicationId);
        
        return ApplicationDetailResponse.builder()
                .applicationId(applicationId)
                .jobId(job.getJobId())
                .jobTitle(job.getTitle())
                .jobDescription(job.getDescription())
                .jobRequirement(job.getRequirement())
                .candidateId(application.getCandidateId())
                .candidateName(candidate != null ? candidate.getName() : "Unknown")
                .candidateEmail(candidate != null ? candidate.getEmail() : "Unknown")
                .candidatePhone(candidate != null ? candidate.getPhone() : null)
                .status(application.getStatus())
                .statusDisplay(getStatusDisplay(application.getStatus()))
                .note(application.getNote())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .history(history)
                .upcomingInterview(upcomingInterview)
                .build();
    }

    /**
     * 4. Cập nhật trạng thái ứng tuyển
     */
    @Transactional
    public void updateStatus(UUID applicationId, UpdateStatusRequest request, UUID employerId) {
        JobApplication application = getApplicationWithPermission(applicationId, employerId);
        String oldStatus = application.getStatus();
        String newStatus = request.getStatus();
        
        validateStatusTransition(oldStatus, newStatus);
        
        application.setStatus(newStatus);
        if (request.getNote() != null) {
            application.setNote(request.getNote());
        }
        application.setUpdatedAt(Instant.now());
        jobApplicationRepository.save(application);
        
        // Lưu lịch sử
        saveStatusHistory(application, oldStatus, newStatus, employerId, request.getNote());
        
        // Gửi thông báo
        notificationService.notifyStatusChange(application, oldStatus, newStatus);
        
        log.info("Updated application {} status from {} to {} by employer {}", 
                 applicationId, oldStatus, newStatus, employerId);
    }

    /**
     * 5. Lấy lịch sử thay đổi
     */
    @Transactional(readOnly = true)
    public List<StatusHistoryResponse> getApplicationHistory(UUID applicationId, UUID employerId) {
        getApplicationWithPermission(applicationId, employerId);
        
        List<ApplicationStatusHistory> histories = historyRepository
                .findByApplicationIdOrderByCreatedAtAsc(applicationId);
        
        return histories.stream()
                .map(h -> StatusHistoryResponse.builder()
                        .oldStatus(h.getOldStatus())
                        .newStatus(h.getNewStatus())
                        .newStatusDisplay(getStatusDisplay(h.getNewStatus()))
                        .changedBy(h.getChangedBy().toString())
                        .note(h.getNote())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 6. Lên lịch phỏng vấn
     */
    @Transactional
    public InterviewScheduleResponse scheduleInterview(UUID applicationId, InterviewScheduleRequest request, UUID employerId) {
        JobApplication application = getApplicationWithPermission(applicationId, employerId);
        
        InterviewSchedule schedule = InterviewSchedule.builder()
                .applicationId(applicationId)
                .scheduledTime(request.getScheduledTime())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60)
                .meetingLink(request.getMeetingLink())
                .meetingPassword(request.getMeetingPassword())
                .location(request.getLocation())
                .interviewType(request.getInterviewType() != null ? request.getInterviewType() : "TECHNICAL")
                .interviewerName(request.getInterviewerName())
                .interviewerEmail(request.getInterviewerEmail())
                .status("SCHEDULED")
                .notes(request.getNotes())
                .createdBy(employerId)
                .build();
        
        InterviewSchedule saved = interviewScheduleRepository.save(schedule);
        
        notificationService.notifyInterviewScheduled(application, request.getScheduledTime(), request.getMeetingLink());
        
        return mapToInterviewScheduleResponse(saved);
    }

    /**
     * 7. Hủy lịch phỏng vấn
     */
    @Transactional
    public void cancelInterview(UUID scheduleId, String reason, UUID employerId) {
        InterviewSchedule schedule = interviewScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch phỏng vấn"));
        
        schedule.setStatus("CANCELLED");
        schedule.setCancelledBy(employerId);
        schedule.setCancelledReason(reason);
        interviewScheduleRepository.save(schedule);
        
        log.info("Interview schedule {} cancelled by employer {}", scheduleId, employerId);
    }

    /**
     * Lấy danh sách jobs của employer hiện tại
     */
    @Transactional(readOnly = true)
    public List<EmployerJobResponse> getMyJobs(UUID employerId) {
        // Lấy tất cả jobs do employer tạo
        List<Job> jobs = jobRepository.findByCreatedBy(employerId);

        return jobs.stream()
                .map(job -> EmployerJobResponse.builder()
                        .jobId(job.getJobId())
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .status(job.getStatus())
                        .applicationCount(job.getApplicationCount() != null ? job.getApplicationCount() : 0)
                        .viewCount(job.getViewCount() != null ? job.getViewCount() : 0)
                        .createdAt(job.getCreatedAt())
                        .updatedAt(job.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết job của employer
     */
    @Transactional(readOnly = true)
    public EmployerJobResponse getJobDetail(UUID jobId, UUID employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc"));

        // Kiểm tra quyền sở hữu
        if (!job.getCreatedBy().equals(employerId)) {
            throw new UnauthorizedException("Bạn không có quyền xem công việc này");
        }

        return EmployerJobResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .description(job.getDescription())
                .status(job.getStatus())
                .applicationCount(job.getApplicationCount() != null ? job.getApplicationCount() : 0)
                .viewCount(job.getViewCount() != null ? job.getViewCount() : 0)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void verifyJobAccess(UUID jobId, UUID employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc"));
        
        if (!job.getCreatedBy().equals(employerId)) {
            throw new UnauthorizedException("Bạn không có quyền truy cập công việc này");
        }
    }

    private JobApplication getApplicationWithPermission(UUID applicationId, UUID employerId) {
        JobApplication application = jobApplicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển"));
        
        verifyJobAccess(application.getJobId(), employerId);
        return application;
    }

    private void validateStatusTransition(String oldStatus, String newStatus) {
        if ((STATUS_ACCEPTED.equals(oldStatus) || STATUS_REJECTED.equals(oldStatus)) 
                && !oldStatus.equals(newStatus)) {
            throw new BadRequestException("Không thể thay đổi trạng thái sau khi đã kết thúc");
        }
        
        if (STATUS_CANCELLED.equals(oldStatus)) {
            throw new BadRequestException("Không thể thay đổi trạng thái của đơn đã hủy");
        }
    }

    private void saveStatusHistory(JobApplication application, String oldStatus, String newStatus, UUID changedBy, String note) {
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .applicationId(application.getApplicationId())
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .note(note)
                .build();
        historyRepository.save(history);
    }

    private boolean hasUpcomingInterview(UUID applicationId) {
        List<InterviewSchedule> schedules = interviewScheduleRepository.findUpcomingByApplicationId(applicationId);
        return !schedules.isEmpty();
    }

    private InterviewScheduleResponse getUpcomingInterview(UUID applicationId) {
        List<InterviewSchedule> schedules = interviewScheduleRepository.findUpcomingByApplicationId(applicationId);
        if (!schedules.isEmpty()) {
            return mapToInterviewScheduleResponse(schedules.get(0));
        }
        return null;
    }

    private InterviewScheduleResponse mapToInterviewScheduleResponse(InterviewSchedule schedule) {
        return InterviewScheduleResponse.builder()
                .id(schedule.getId())
                .scheduledTime(schedule.getScheduledTime())
                .durationMinutes(schedule.getDurationMinutes())
                .meetingLink(schedule.getMeetingLink())
                .meetingPassword(schedule.getMeetingPassword())
                .location(schedule.getLocation())
                .interviewType(schedule.getInterviewType())
                .interviewerName(schedule.getInterviewerName())
                .interviewerEmail(schedule.getInterviewerEmail())
                .status(schedule.getStatus())
                .notes(schedule.getNotes())
                .build();
    }

    // Helper method chuyển S3 URI sang MinIO URL
    private String convertS3UriToMinioUrl(String s3Uri) {
        if (s3Uri == null || !s3Uri.startsWith("s3://")) return null;

        // s3://recruitment-files/candidates/87b16a80-27ae-4aac-921a-85c73559e587/cv/filename.pdf
        String minioBaseUrl = "http://localhost:9001/browser/";

        // Bỏ "s3://"
        String withoutScheme = s3Uri.substring(5);

        // Tìm vị trí slash đầu tiên để tách bucket và path
        int firstSlash = withoutScheme.indexOf('/');
        if (firstSlash == -1) return null;

        String bucket = withoutScheme.substring(0, firstSlash);        // recruitment-files
        String path = withoutScheme.substring(firstSlash + 1);         // candidates/.../cv/filename.pdf

        // Encode path (chuyển / thành %2F)
        String encodedPath = path.replace("/", "%2F");

        return minioBaseUrl + bucket + "/" + encodedPath;
    }

    private String getStatusDisplay(String status) {
        return switch (status) {
            case STATUS_PENDING -> "Đang xử lý";
            case STATUS_REVIEWED -> "Đã xem";
            case STATUS_INTERVIEW -> "Phỏng vấn";
            case STATUS_ACCEPTED -> "Được nhận";
            case STATUS_REJECTED -> "Từ chối";
            default -> status;
        };
    }
}