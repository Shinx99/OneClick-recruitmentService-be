package com.onceClick.recruitmentService.shared.notification;


import java.util.Map;

public interface EmailTemplateService {
    String buildEmployerVerification(Map<String, Object> vars);
    String buildCompanyVerified(Map<String, Object> vars);
    String buildCompanyCreated(Map<String, Object> vars);
    String buildCandidateWelcome(Map<String, Object> vars);
    String buildJobPublished(Map<String, Object> vars);
    String buildResumeConfirm(Map<String, Object> vars);
    String buildApplicationStatusUpdate(Map<String, Object> vars);
    String buildInterviewScheduled(Map<String, Object> vars);
}
