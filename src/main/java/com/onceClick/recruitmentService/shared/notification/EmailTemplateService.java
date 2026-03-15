package com.onceClick.recruitmentService.shared.notification;

/**
 * Email template builder service
 * Responsible for generating HTML content from Thymeleaf templates
 */
public interface EmailTemplateService {

    String buildVerificationEmail(String username, String verifyUrl, int expiryHours, String supportUrl);

    String buildPasswordResetEmail(String username, String resetUrl, int expiryMinutes, String supportUrl);

    String buildOtpEmail(String username, String otpCode, int expiryMinutes, String supportUrl);

    String buildPasswordChangedEmail(String username, String ipAddress, String securityUrl);

    String buildWelcomeEmail(String username, String dashboardUrl, String docsUrl);

    String buildSuspiciousLoginEmail(String username, String ipAddress, String location, String changePasswordUrl);
}
