package com.onceClick.recruitmentService.features.jobApplication.service;

import com.onceClick.recruitmentService.shared.constant.ApplicationConstants;
import com.onceClick.recruitmentService.shared.notification.EmailService;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.entity.JobApplication;
import com.onceClick.recruitmentService.shared.persistence.entity.Notification;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.onceClick.recruitmentService.shared.constant.ApplicationConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional
    public void notifyNewApplication(JobApplication application, String jobTitle, UUID employerId) {
        Notification notification = Notification.builder()
                .userId(employerId)
                .type(NOTIF_NEW_APPLICATION)
                .title("Có ứng viên mới ứng tuyển")
                .content(String.format("Ứng viên đã ứng tuyển vào vị trí %s", jobTitle))
                .relatedApplicationId(application.getApplicationId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        log.info("Created NEW_APPLICATION notification for employer {}", employerId);
    }

    @Transactional
    public void notifyStatusChange(JobApplication application, String oldStatus, String newStatus) {
        Candidate candidate = candidateRepository.findById(application.getCandidateId()).orElse(null);
        Job job = jobRepository.findById(application.getJobId()).orElse(null);

        if (candidate == null || job == null) {
            log.warn("Cannot send notification: candidate or job not found for application {}",
                    application.getApplicationId());
            return;
        }

        String statusDisplay = getStatusDisplay(newStatus);

        // Gửi email thông báo
        emailService.sendApplicationStatusUpdate(
                candidate.getEmail(),
                candidate.getName(),
                job.getTitle(),
                newStatus,
                statusDisplay,
                application.getNote()
        );

        log.info("Status change notification sent for application {}: {} -> {}",
                application.getApplicationId(), oldStatus, newStatus);
    }


    @Transactional
    public void notifyInterviewScheduled(JobApplication application, Instant scheduledTime, String meetingLink) {
        Candidate candidate = candidateRepository.findById(application.getCandidateId()).orElse(null);
        Job job = jobRepository.findById(application.getJobId()).orElse(null);

        if (candidate == null || job == null) {
            log.warn("Cannot send interview notification: candidate or job not found");
            return;
        }

        // Format thời gian
        LocalDateTime localDateTime = LocalDateTime.ofInstant(scheduledTime, ZoneId.systemDefault());
        String formattedTime = localDateTime.format(DATE_FORMATTER);

        // Gửi email mời phỏng vấn
        emailService.sendInterviewScheduled(
                candidate.getEmail(),
                candidate.getName(),
                job.getTitle(),
                formattedTime,
                meetingLink,
                null
        );

        log.info("Interview invitation sent for application {} to candidate {}",
                application.getApplicationId(), candidate.getEmail());
    }



    private String getStatusNotificationTitle(String status) {
        return switch (status) {
            case STATUS_REVIEWED -> "Hồ sơ đã được xem";
            case STATUS_INTERVIEW -> "Được mời phỏng vấn";
            case STATUS_ACCEPTED -> "Chúc mừng bạn đã trúng tuyển";
            case STATUS_REJECTED -> "Thông báo kết quả ứng tuyển";
            default -> "Cập nhật trạng thái hồ sơ";
        };
    }

    private String getStatusNotificationContent(String status) {
        return switch (status) {
            case STATUS_REVIEWED -> "Hồ sơ của bạn đã được nhà tuyển dụng xem xét";
            case STATUS_INTERVIEW -> "Vui lòng kiểm tra lịch phỏng vấn trong hệ thống";
            case STATUS_ACCEPTED -> "Chúc mừng! Hãy kiểm tra email để biết thêm chi tiết";
            case STATUS_REJECTED -> "Rất tiếc, hồ sơ của bạn không phù hợp. Chúc bạn may mắn ở cơ hội khác";
            default -> "Trạng thái hồ sơ của bạn đã được cập nhật";
        };
    }

    private String getStatusDisplay(String status) {
        return switch (status) {
            case "pending" -> "Đang xử lý";
            case "reviewed" -> "Đã xem";
            case "interview" -> "Phỏng vấn";
            case "accepted" -> "Được nhận";
            case "rejected" -> "Từ chối";
            default -> status;
        };
    }
}