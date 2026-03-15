package com.onceClick.recruitmentService.shared.notification;

/*
* Email service interface (internal infrastructure)
* just use at infrastructure layer
* */

public interface EmailService {

    void sendVerificationEmail(String toEmail, String username,
                               String verificationToken, int expiryHours);

    void sendPasswordResetEmail(String toEmail, String username,
                                String resetToken, int expiryMinutes);

    void sendOtpEmail(String toEmail, String username,
                      String otpCode, int expiryMinutes);

    void sendPasswordChangedConfirmation(String toEmail, String username);

    void sendWelcomeEmail(String toEmail, String username);

    void sendSuspiciousLoginAlert(String toEmail, String username,
                                  String ipAddress, String location);


}
