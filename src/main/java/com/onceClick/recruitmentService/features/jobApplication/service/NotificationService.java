package com.onceClick.recruitmentService.features.jobApplication.service;

import com.onceClick.recruitmentService.shared.constant.ApplicationConstants;
import com.onceClick.recruitmentService.shared.persistence.entity.JobApplication;
import com.onceClick.recruitmentService.shared.persistence.entity.Notification;
import com.onceClick.recruitmentService.shared.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static com.onceClick.recruitmentService.shared.constant.ApplicationConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

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
        Notification notification = Notification.builder()
                .userId(application.getCandidateId())
                .type(NOTIF_STATUS_CHANGED)
                .title(getStatusNotificationTitle(newStatus))
                .content(getStatusNotificationContent(newStatus))
                .relatedApplicationId(application.getApplicationId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        log.info("Created STATUS_CHANGED notification for candidate {}", application.getCandidateId());
    }

    @Transactional
    public void notifyInterviewScheduled(JobApplication application, Instant scheduledTime, String meetingLink) {
        Notification notification = Notification.builder()
                .userId(application.getCandidateId())
                .type(NOTIF_INTERVIEW_SCHEDULED)
                .title("Lịch phỏng vấn đã được sắp xếp")
                .content(String.format("Bạn có lịch phỏng vấn lúc %s. Link: %s", scheduledTime, meetingLink))
                .relatedApplicationId(application.getApplicationId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        log.info("Created INTERVIEW_SCHEDULED notification for candidate {}", application.getCandidateId());
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
}