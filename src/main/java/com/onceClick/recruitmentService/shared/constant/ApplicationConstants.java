package com.onceClick.recruitmentService.shared.constant;

public final class ApplicationConstants {
    
    private ApplicationConstants() {} // Prevent instantiation
    
    // ========== APPLICATION STATUS ==========
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_REVIEWED = "reviewed";
    public static final String STATUS_INTERVIEW = "interview";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
    public static final String STATUS_CANCELLED = "cancelled";
    
    // ========== NOTIFICATION TYPES ==========
    public static final String NOTIF_NEW_APPLICATION = "NEW_APPLICATION";
    public static final String NOTIF_STATUS_CHANGED = "STATUS_CHANGED";
    public static final String NOTIF_INTERVIEW_SCHEDULED = "INTERVIEW_SCHEDULED";
    public static final String NOTIF_INTERVIEW_REMINDER = "INTERVIEW_REMINDER";
    public static final String NOTIF_APPLICATION_CANCELLED = "APPLICATION_CANCELLED";
    public static final String NOTIF_OFFER_SENT = "OFFER_SENT";
    
    // ========== INTERVIEW TYPES ==========
    public static final String INTERVIEW_TECHNICAL = "TECHNICAL";
    public static final String INTERVIEW_HR = "HR";
    public static final String INTERVIEW_MANAGER = "MANAGER";
    public static final String INTERVIEW_PRESENTATION = "PRESENTATION";
    public static final String INTERVIEW_TEST = "TEST";
    
    // ========== INTERVIEW STATUS ==========
    public static final String INTERVIEW_STATUS_SCHEDULED = "SCHEDULED";
    public static final String INTERVIEW_STATUS_COMPLETED = "COMPLETED";
    public static final String INTERVIEW_STATUS_CANCELLED = "CANCELLED";
    public static final String INTERVIEW_STATUS_RESCHEDULED = "RESCHEDULED";
    
    // ========== INTERVIEW RESULT ==========
    public static final String INTERVIEW_RESULT_PASS = "PASS";
    public static final String INTERVIEW_RESULT_FAIL = "FAIL";
    public static final String INTERVIEW_RESULT_PENDING = "PENDING";
}