package com.onceClick.recruitmentService.shared.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final TemplateEngine templateEngine;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.name:OneClick Recruitment}")
    private String appName;

    @Value("${app.logo.url:https://oneclick.vn/logo.png}")
    private String logoUrl;

    @Value("${app.support.email:support@oneclick.vn}")
    private String supportEmail;

    @Value("${app.company.phone:19001234}")
    private String companyPhone;

    // ========== EMPLOYER ==========
    @Override
    public String buildEmployerVerification(Map<String, Object> vars) {
        log.debug("Building employer-verification template");
        Context ctx = new Context();
        ctx.setVariables(createBaseVariables());
        ctx.setVariables(vars);
        return templateEngine.process("email/employer-verification", ctx);
    }

    // ========== COMPANY ==========
    @Override
    public String buildCompanyVerified(Map<String, Object> vars) {
        log.debug("Building company-verified template");
        Context ctx = new Context();
        ctx.setVariables(createBaseVariables());
        ctx.setVariables(vars);
        return templateEngine.process("email/company-verified", ctx);
    }

    @Override
    public String buildCompanyCreated(Map<String, Object> vars) {
        log.debug("Building company-created template");
        Context ctx = new Context();
        ctx.setVariables(createBaseVariables());
        ctx.setVariables(vars);
        return templateEngine.process("email/company-created", ctx);
    }

    // ========== CANDIDATE ==========
    @Override
    public String buildCandidateWelcome(Map<String, Object> vars) {
        log.debug("Building candidate-welcome template");
        Context ctx = new Context();
        ctx.setVariables(createBaseVariables());
        ctx.setVariables(vars);
        return templateEngine.process("email/candidate-welcome", ctx);
    }

    // ========== JOB ==========
    @Override
    public String buildJobPublished(Map<String, Object> vars) {
        log.debug("Building job-published template");
        Context ctx = new Context();
        ctx.setVariables(createBaseVariables());
        ctx.setVariables(vars);
        return templateEngine.process("email/job-published", ctx);
    }

    // ========== RESUME ==========
    @Override
    public String buildResumeConfirm(Map<String, Object> vars) {
        log.debug("Building resume-confirm template");
        Context ctx = new Context();
        ctx.setVariables(createBaseVariables());
        ctx.setVariables(vars);
        return templateEngine.process("email/resume-confirm", ctx);
    }

    // ============ Private Helper Methods ============
    private Map<String, Object> createBaseVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("appName", appName);
        variables.put("logoUrl", logoUrl);
        variables.put("supportEmail", supportEmail);
        variables.put("companyPhone", companyPhone);
        variables.put("helpUrl", frontendUrl + "/help");
        variables.put("privacyUrl", frontendUrl + "/privacy");
        variables.put("termsUrl", frontendUrl + "/terms");
        variables.put("facebookUrl", "https://facebook.com/oneclickvn");
        variables.put("linkedinUrl", "https://linkedin.com/company/oneclick-vn");
        variables.put("currentYear", LocalDateTime.now().getYear());
        return variables;
    }
}
