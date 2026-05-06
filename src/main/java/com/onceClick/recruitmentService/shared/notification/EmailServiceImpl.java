package com.onceClick.recruitmentService.shared.notification;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;



/*
* Email service implementation = SMTP based
* Implements actual email sending logic
* */

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Value("${app.name:OneClick}")
    private String appName;

    // ========== EMPLOYER ==========
    @Override
    public void sendEmployerVerificationEmail(String toEmail, String employerName, String verificationUrl, int expiryHours) {
        Map<String, Object> vars = Map.of(
                "employerName", employerName,
                "verificationUrl", verificationUrl,
                "expiryHours", expiryHours
        );
        String html = emailTemplateService.buildEmployerVerification(vars);
        sendHtmlEmail(toEmail, "✅ Xác thực tài khoản Nhà tuyển dụng", html);
        log.info("[EMPLOYER] Verification email sent to: {}", toEmail);
    }

    @Override
    public void sendCompanyVerifiedEmail(String toEmail, String employerName, String companyName) {
        Map<String, Object> vars = Map.of(
                "employerName", employerName,
                "companyName", companyName,
                "dashboardUrl", frontendUrl + "/employer/dashboard"
        );
        String html = emailTemplateService.buildCompanyVerified(vars);
        sendHtmlEmail(toEmail, "🎉 Công ty của bạn đã được xác thực!", html);
        log.info("[COMPANY] Verified email sent to: {}", toEmail);
    }

    // ========== COMPANY ==========
    @Override
    public void sendCompanyCreatedEmail(String toEmail, String employerName, String companyName, String dashboardUrl) {
        Map<String, Object> vars = Map.of(
                "employerName", employerName,
                "companyName", companyName,
                "dashboardUrl", dashboardUrl
        );
        String html = emailTemplateService.buildCompanyCreated(vars);
        sendHtmlEmail(toEmail, "🏢 Hồ sơ công ty đã được tạo thành công", html);
    }

    // ========== CANDIDATE ==========
    @Override
    public void sendCandidateWelcomeEmail(String toEmail, String candidateName, String dashboardUrl) {
        Map<String, Object> vars = Map.of(
                "candidateName", candidateName,
                "dashboardUrl", dashboardUrl
        );
        String html = emailTemplateService.buildCandidateWelcome(vars);
        sendHtmlEmail(toEmail, "👋 Chào mừng đến với OneClick Recruitment!", html);
    }

    // ========== JOB ==========
    @Override
    public void sendJobPublishedEmail(String toEmail, String employerName, String jobTitle, String jobUrl) {
        Map<String, Object> vars = Map.of(
                "employerName", employerName,
                "jobTitle", jobTitle,
                "jobUrl", jobUrl
        );
        String html = emailTemplateService.buildJobPublished(vars);
        sendHtmlEmail(toEmail, "🚀 Việc làm của bạn đã được đăng!", html);
    }

    // ========== RESUME ==========
    @Override
    public void sendResumeUploadConfirm(String toEmail, String candidateName, String resumeUrl) {
        Map<String, Object> vars = Map.of(
                "candidateName", candidateName,
                "resumeUrl", resumeUrl,
                "dashboardUrl", frontendUrl + "/candidate/resumes"
        );
        String html = emailTemplateService.buildResumeConfirm(vars);
        sendHtmlEmail(toEmail, "📄 CV của bạn đã được upload thành công", html);
    }

    @Override
    public void sendSupportEmail(String toEmail, String subject, String message) {
        Map<String, Object> vars = Map.of("message", message);
        String html = emailTemplateService.buildEmployerVerification(vars); // fallback
        sendHtmlEmail(toEmail, subject, html);
    }

    // ============ Private Helper Method ============

    /**
     * Core method to send HTML email
     */

    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("❌ [EMAIL] Failed to send email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendApplicationStatusUpdate(String toEmail, String candidateName, String jobTitle,
                                            String status, String statusDisplay, String note) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("candidateName", candidateName != null ? candidateName : "Ứng viên");
        vars.put("jobTitle", jobTitle != null ? jobTitle : "Việc làm");
        vars.put("status", status != null ? status : "");
        vars.put("statusDisplay", statusDisplay != null ? statusDisplay : "Đã cập nhật");
        vars.put("note", note != null ? note : "");
        vars.put("dashboardUrl", frontendUrl + "/applications");

        String subject = getSubjectByStatus(statusDisplay);
        String html = emailTemplateService.buildApplicationStatusUpdate(vars);
        sendHtmlEmail(toEmail, subject, html);
        log.info("[APPLICATION] Status update email sent to: {} for job: {} - status: {}",
                toEmail, jobTitle, statusDisplay);
    }

    @Override
    public void sendInterviewScheduled(String toEmail, String candidateName, String jobTitle,
                                       String scheduledTime, String meetingLink, String location) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("candidateName", candidateName != null ? candidateName : "Ứng viên");
        vars.put("jobTitle", jobTitle != null ? jobTitle : "Việc làm");
        vars.put("scheduledTime", scheduledTime != null ? scheduledTime : "Chưa cập nhật");
        vars.put("meetingLink", meetingLink != null ? meetingLink : "");
        vars.put("location", location != null ? location : "Online");
        vars.put("dashboardUrl", frontendUrl + "/applications");

        String html = emailTemplateService.buildInterviewScheduled(vars);
        sendHtmlEmail(toEmail, "📅 Thư mời phỏng vấn từ " + appName, html);
        log.info("[INTERVIEW] Interview invitation sent to: {} for job: {}", toEmail, jobTitle);
    }

    private String getSubjectByStatus(String statusDisplay) {
        return switch (statusDisplay) {
            case "Phỏng vấn" -> "📅 Thư mời phỏng vấn";
            case "Được nhận" -> "🎉 Chúc mừng! Bạn đã trúng tuyển";
            case "Từ chối" -> "📧 Cập nhật kết quả ứng tuyển";
            default -> "📧 Cập nhật trạng thái đơn ứng tuyển";
        };
    }
}
