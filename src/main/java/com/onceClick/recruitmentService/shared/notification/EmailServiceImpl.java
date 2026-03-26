package com.onceClick.recruitmentService.shared.notification;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
}
