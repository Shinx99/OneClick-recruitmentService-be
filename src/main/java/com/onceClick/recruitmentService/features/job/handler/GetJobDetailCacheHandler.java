package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.response.GetJobDetailResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.entity.JobSkills;
import com.onceClick.recruitmentService.shared.persistence.entity.Skills;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobSkillRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.SkillsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetJobDetailCacheHandler {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillsRepository skillsRepository;

    @Cacheable(value = "job:by-job-id", key = "#jobId")
    @Transactional  //(readOnly = true)
    public ApiResponse<GetJobDetailResponseDto> getJobDetail(UUID jobId, HttpServletRequest request) {

        log.info("Fetching job detail for jobId: {}", jobId);

        // 1. Tìm Job theo jobId — ném ResourceNotFoundException nếu không tồn tại
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "jobId", jobId));

        // 2. Lấy thông tin Company (null-safe nếu company đã bị xoá)
        Company company = companyRepository.findById(job.getCompanyId()).orElse(null);

        // 3. Lấy danh sách Skills qua bảng trung gian job_skills
        List<GetJobDetailResponseDto.SkillInfo> skillInfos = getSkillsForJob(jobId);

        // 4. Map sang DTO
        GetJobDetailResponseDto responseDto = new GetJobDetailResponseDto(
                job.getJobId(),
                job.getCompanyId(),
                company != null ? company.getCompanyName() : null,
                company != null ? company.getLogoUrl() : null,
                job.getTitle(),
                job.getDescription(),
                job.getRequirement(),
                job.getMajorPreferred(),
                job.getLevel(),
                job.getJobType(),
                job.getProvince(),
                job.getCommune(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getExperienceMinYear(),
                job.getApplicationDeadline(),
                job.getApplicationCount(),
                job.getViewCount(),
                job.getStatus(),
                job.getImgUrl(),
                skillInfos,
                job.getCreatedBy(),
                job.getUpdatedBy(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );

        return ApiResponse.success("Lấy chi tiết công việc thành công", responseDto);
    }

    private List<GetJobDetailResponseDto.SkillInfo> getSkillsForJob(UUID jobId) {
        // 3a. Query bảng job_skills theo jobId
        List<JobSkills> jobSkills = jobSkillRepository.findByIdJobId(jobId);

        if (jobSkills.isEmpty()) {
            return Collections.emptyList();
        }

        // 3b. Extract danh sách skillsId từ composite key
        List<UUID> skillIds = jobSkills.stream()
                .map(js -> js.getId().getSkillsId())
                .toList();

        // 3c. Batch fetch Skills (1 query thay vì N queries)
        List<Skills> skills = skillsRepository.findAllById(skillIds);

        // 3d. Map sang SkillInfo records
        return skills.stream()
                .map(skill -> new GetJobDetailResponseDto.SkillInfo(skill.getSkillsId(), skill.getSkillsName()))
                .toList();
    }

}
