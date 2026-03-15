package com.onceClick.recruitmentService.shared.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService{

    private final TemplateEngine templateEngine;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.name:OneClick}")
    private String appName;

    @Value("${app.logo.url:https://yourapp.com/logo.png}")
    private String logoUrl;

    @Value("${app.support.email:support@oneclick.com}")
    private String supportEmail;

    @Override
    public String buildVerificationEmail(String username, String verifyUrl, int expiryHours, String supportUrl) {
        log.debug("Building verification email template for: {}", username);

        Map<String, Object> variables = createBaseVariables();
        variables.put("username", username);
        variables.put("verifyUrl", verifyUrl);
        variables.put("expiryHours", expiryHours);
        variables.put("supportUrl", supportUrl);

        return processTemplate("email-verification", variables);
    }

    @Override
    public String buildPasswordResetEmail(String username, String resetUrl, int expiryMinutes, String supportUrl) {
        log.debug("Building password reset email template for: {}", username);

        Map<String, Object> variables = createBaseVariables();
        variables.put("username", username);
        variables.put("resetUrl", resetUrl);
        variables.put("expiryMinutes", expiryMinutes);
        variables.put("supportUrl", supportUrl);

        return processTemplate("password-reset", variables);    }

    @Override
    public String buildOtpEmail(String username, String otpCode, int expiryMinutes, String supportUrl) {
        log.debug("Building OTP email template for: {}", username);

        Map<String, Object> variables = createBaseVariables();
        variables.put("username", username);
        variables.put("otpCode", otpCode);
        variables.put("expiryMinutes", expiryMinutes);
        variables.put("supportUrl", supportUrl);

        return processTemplate("otp-email", variables);
    }

    @Override
    public String buildPasswordChangedEmail(String username, String ipAddress, String securityUrl) {
        log.debug("Building password changed email template for: {}", username);

        Map<String, Object> variables = createBaseVariables();
        variables.put("username", username);
        variables.put("changedAt", LocalDateTime.now());
        variables.put("ipAddress", ipAddress);
        variables.put("securityUrl", securityUrl);

        return processTemplate("password-changed", variables);
    }

    @Override
    public String buildWelcomeEmail(String username, String dashboardUrl, String docsUrl) {
        log.debug("Building welcome email template for: {}", username);

        Map<String, Object> variables = createBaseVariables();
        variables.put("username", username);
        variables.put("dashboardUrl", dashboardUrl);
        variables.put("docsUrl", docsUrl);
        variables.put("communityUrl", frontendUrl + "/community");
        variables.put("tutorialsUrl", frontendUrl + "/tutorials");
        variables.put("faqUrl", frontendUrl + "/faq");

        return processTemplate("welcome", variables);
    }

    @Override
    public String buildSuspiciousLoginEmail(String username, String ipAddress, String location, String changePasswordUrl) {
        log.debug("Building suspicious login email template for: {}", username);

        Map<String, Object> variables = createBaseVariables();
        variables.put("username", username);
        variables.put("ipAddress", ipAddress);
        variables.put("location", location);
        variables.put("loginTime", LocalDateTime.now());
        variables.put("deviceInfo", "Unknown Device");
        variables.put("changePasswordUrl", changePasswordUrl);

        return processTemplate("suspicious-login", variables);
    }

    // ============ Private Helper Methods ============

    private Map<String, Object> createBaseVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", appName);
        variables.put("logoUrl", logoUrl);
        variables.put("helpUrl", frontendUrl + "/help");
        variables.put("privacyUrl", frontendUrl + "/privacy");
        variables.put("termsUrl", frontendUrl + "/terms");
        variables.put("facebookUrl", "https://facebook.com/yourapp");
        variables.put("twitterUrl", "https://twitter.com/yourapp");
        variables.put("linkedinUrl", "https://linkedin.com/company/yourapp");
        return variables;
    }

    private String processTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process("email/" + templateName, context);
    }
}
