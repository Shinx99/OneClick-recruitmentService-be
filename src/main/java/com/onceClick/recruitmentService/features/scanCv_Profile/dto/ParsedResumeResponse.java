package com.onceClick.recruitmentService.features.scanCv_Profile.dto;

import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ParsedData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParsedResumeResponse {
    private boolean success;
    private Object data;
    private String message;

    // Static factory methods
    public static ParsedResumeResponse success(UUID resumeId, String pdfUrl, ParsedData parsed) {
        return new ParsedResumeResponse(true,
                Map.of("resumeId", resumeId, "pdfUrl", pdfUrl, "parsed", parsed), null);
    }

    public static ParsedResumeResponse error(String message) {
        return new ParsedResumeResponse(false, null, message);
    }

    public static ParsedResumeResponse warning(String message) {
        return new ParsedResumeResponse(false, null, "warning " + message);
    }

    // WARNING với s3Url (giữ link download)
    public static ParsedResumeResponse warning(String message, String pdfUrl) {
        return new ParsedResumeResponse(false,
                Map.of("pdfUrl", pdfUrl),
                "warning " + message);
    }
}
