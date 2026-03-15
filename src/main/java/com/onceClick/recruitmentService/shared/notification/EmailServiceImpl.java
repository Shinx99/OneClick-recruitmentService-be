package com.onceClick.recruitmentService.shared.notification;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



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

    @Override
    public void sendVerificationEmail(String toEmail, String username, String verificationToken, int expiryHours) {

        String verifyUrl = frontendUrl + "/verify-email?token=" + verificationToken;
        String supportUrl = frontendUrl + "/support";

        String htmlContent = emailTemplateService.buildVerificationEmail(username, verifyUrl, expiryHours, supportUrl);

        sendHtmlEmail(toEmail, "Verify your email address", htmlContent);
        log.info("[EMAIL] Verification email sent to: {}", toEmail);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String resetToken, int expiryMinutes) {

        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
        String supportUrl = frontendUrl + "/support";

        String htmlContent = emailTemplateService.buildPasswordResetEmail(
                username, resetUrl, expiryMinutes, supportUrl
        );

        sendHtmlEmail(toEmail, "Reset your password", htmlContent);
        log.info("[EMAIL] Password reset email sent to: {}", toEmail);
    }

    @Override
    public void sendOtpEmail(String toEmail, String username, String otpCode, int expiryMinutes) {

        String supportUrl = frontendUrl + "/support";

        String htmlContent = emailTemplateService.buildOtpEmail(
                username, otpCode, expiryMinutes, supportUrl
        );

        sendHtmlEmail(toEmail, "Your OTP code", htmlContent);
        log.info("[EMAIL] OTP email sent to: {} (OTP: {}", toEmail, otpCode);

    }

    @Override
    public void sendPasswordChangedConfirmation(String toEmail, String username) {

        String securityUrl = frontendUrl + "/security";

        String htmlContent = emailTemplateService.buildPasswordChangedEmail(
                username, "Unknown", securityUrl
        );
        sendHtmlEmail(toEmail, "Your password has been changed", htmlContent);
        log.info("[EMAIL] Password changed confirmation sent to: {}", toEmail);

    }

    @Override
    public void sendWelcomeEmail(String toEmail, String username) {

        String dashboardUrl = frontendUrl + "/dashboard";
        String docsUrl = frontendUrl + "/docs";

        String htmlContent = emailTemplateService.buildWelcomeEmail(
                username, dashboardUrl, docsUrl
        );

        sendHtmlEmail(toEmail, "Welcome to " + appName + "!", htmlContent);
        log.info("[EMAIL] Welcome email sent to: {}", toEmail);
    }

    @Override
    public void sendSuspiciousLoginAlert(String toEmail, String username, String ipAddress, String location) {

        String changePasswordUrl = frontendUrl + "/change-password";

        String htmlContent = emailTemplateService.buildSuspiciousLoginEmail(
                username, ipAddress, location, changePasswordUrl
        );

        sendHtmlEmail(toEmail, "⚠️ Unusual login detected", htmlContent);
        log.warn("[SECURITY_ALERT] Suspicious login alert sent to: {}", toEmail);

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
