// dto/request/ApplicationFilterRequest.java
package com.onceClick.recruitmentService.features.jobApplication.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.Instant;

@Data
public class ApplicationFilterRequest {
    private String status;           // pending, reviewed, interview, accepted, rejected
    private String keyword;          // Tìm theo tên ứng viên hoặc email
    private String sortBy = "appliedAt";
    private String sortDir = "DESC";
    private Integer page = 0;
    private Integer size = 20;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant fromDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant toDate;
}