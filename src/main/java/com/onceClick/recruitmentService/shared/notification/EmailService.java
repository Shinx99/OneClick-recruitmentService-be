package com.onceClick.recruitmentService.shared.notification;

public interface EmailService {
    // Employer/Company
    void sendEmployerVerificationEmail(String toEmail, String employerName, String verificationUrl, int expiryHours);
    void sendCompanyVerifiedEmail(String toEmail, String employerName, String companyName);
    void sendCompanyCreatedEmail(String toEmail, String employerName, String companyName, String dashboardUrl);

    // Candidate
    void sendCandidateWelcomeEmail(String toEmail, String candidateName, String dashboardUrl);

    // Job
    void sendJobPublishedEmail(String toEmail, String employerName, String jobTitle, String jobUrl);

    // Resume
    void sendResumeUploadConfirm(String toEmail, String candidateName, String resumeUrl);

    // Common
    void sendSupportEmail(String toEmail, String subject, String message);
}
