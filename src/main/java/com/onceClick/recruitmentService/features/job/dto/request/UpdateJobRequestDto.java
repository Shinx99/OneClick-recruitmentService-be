package com.onceClick.recruitmentService.features.job.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJobRequestDto {

    private String title;

    private String description;

    private String requirement;

    private String majorPreferred;

    private String level;

    private String jobType;

    private String province;

    private String commune;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private BigDecimal experienceMinYear;

    private LocalDate applicationDeadline;

    private String status;

    private String imgUrl;

    private List<UUID> skillIds;

    private List<String> skillNames;
}
