package com.onceClick.recruitmentService.features.ai_cv_matcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedCvDto {

    private Personal personal;
    private List<WorkExperience> workExperience;
    private List<Education> education;
    private List<Certificate> certificates;
    private List<String> skills;
    private ExtractedFields extractedFields;

    @Data
    public static class Personal {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String location;     // "HCM/HN"
        private String avatarUrl;
        private String linkedinUrl;
    }

    @Data
    public static class WorkExperience {
        private String company;
        private String position;
        private String startDate;    // "2023-05-01" YYYY-MM-DD
        private String endDate;      // "2024-12-31"
        private String description;
    }

    @Data
    public static class Education {
        private String schoolName;
        private String degree;       // "Cao đẳng CNTT"
        private String fieldOfStudy;
        private String startDate;    // "2022-09" YYYY-MM
        private String endDate;      // "2025-12"
        private Boolean isCurrent;
    }

    @Data
    public static class Certificate {
        private String name;
        private String issuer;
        private String issueDate;
        private String expiryDate;
    }

    @Data
    public static class ExtractedFields {
        private String careerGoal;
        private String major;
        private Double experienceYear;       // 1.5
        private String salaryExpectation;    // "15-20tr"
        private Integer totalExperienceMonths; // 18
        private List<String> topSkills;      // ["Java", "Spring", "Docker"]
    }
}