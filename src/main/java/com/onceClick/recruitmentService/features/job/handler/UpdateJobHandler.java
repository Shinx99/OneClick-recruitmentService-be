package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.request.UpdateJobRequestDto;
import com.onceClick.recruitmentService.features.job.dto.response.CreateJobResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ForbiddenException;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.*;
import com.onceClick.recruitmentService.shared.persistence.handler.SkillsHandler;
import com.onceClick.recruitmentService.shared.persistence.repository.JobEmployerRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobSkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateJobHandler {

    private final JobRepository jobRepository;
    private final JobEmployerRepository jobEmployerRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillsHandler skillsHandler;

    @Transactional
    public ApiResponse<CreateJobResponseDto> updateJob(UUID jobId, UpdateJobRequestDto requestDto, UUID employerId) {

        // 1. Tìm job theo ID
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        // 2. Kiểm tra quyền: employer phải là owner của job
        JobEmployerId jobEmployerIdKey = JobEmployerId.builder()
                .jobId(jobId)
                .employerId(employerId)
                .build();

        JobEmployer jobEmployer = jobEmployerRepository.findById(jobEmployerIdKey)
                .orElseThrow(() -> new ForbiddenException("job", "update"));

        if (!"owner".equalsIgnoreCase(jobEmployer.getAccessRole())) {
            throw new ForbiddenException("Only job owner can update this job");
        }

        // 3. Update các field - chỉ update field nào được gửi lên (!= null)
        if (requestDto.getTitle() != null) {
            job.setTitle(requestDto.getTitle());
        }
        if (requestDto.getDescription() != null) {
            job.setDescription(requestDto.getDescription());
        }
        if (requestDto.getRequirement() != null) {
            job.setRequirement(requestDto.getRequirement());
        }
        if (requestDto.getMajorPreferred() != null) {
            job.setMajorPreferred(requestDto.getMajorPreferred());
        }
        if (requestDto.getLevel() != null) {
            job.setLevel(requestDto.getLevel());
        }
        if (requestDto.getJobType() != null) {
            job.setJobType(requestDto.getJobType());
        }
        if (requestDto.getProvince() != null) {
            job.setProvince(requestDto.getProvince());
        }
        if (requestDto.getCommune() != null) {
            job.setCommune(requestDto.getCommune());
        }
        if (requestDto.getSalaryMin() != null) {
            job.setSalaryMin(requestDto.getSalaryMin());
        }
        if (requestDto.getSalaryMax() != null) {
            job.setSalaryMax(requestDto.getSalaryMax());
        }
        if (requestDto.getExperienceMinYear() != null) {
            job.setExperienceMinYear(requestDto.getExperienceMinYear());
        }
        if (requestDto.getApplicationDeadline() != null) {
            job.setApplicationDeadline(requestDto.getApplicationDeadline());
        }
        if (requestDto.getStatus() != null) {
            job.setStatus(requestDto.getStatus());
        }
        if (requestDto.getImgUrl() != null) {
            job.setImgUrl(requestDto.getImgUrl());
        }

        // Set người cập nhật
        job.setUpdatedBy(employerId);

        // 4. Xử lý Skills (nếu có gửi lên)
        boolean hasSkillIds = requestDto.getSkillIds() != null && !requestDto.getSkillIds().isEmpty();
        boolean hasSkillNames = requestDto.getSkillNames() != null && !requestDto.getSkillNames().isEmpty();

        if (hasSkillIds || hasSkillNames) {
            // Xóa tất cả skills cũ của job
            List<JobSkills> oldSkills = jobSkillRepository.findByIdJobId(jobId);
            jobSkillRepository.deleteAll(oldSkills);

            // Thêm skills theo ID có sẵn
            if (hasSkillIds) {
                for (UUID skillsId : requestDto.getSkillIds()) {
                    JobSkillsId jobSkillsId = new JobSkillsId(jobId, skillsId);
                    JobSkills jobSkills = new JobSkills(jobSkillsId);
                    jobSkillRepository.save(jobSkills);
                }
            }

            // Thêm skills theo tên (tạo mới nếu chưa tồn tại)
            if (hasSkillNames) {
                for (String skillName : requestDto.getSkillNames()) {
                    if (skillName != null && !skillName.trim().isEmpty()) {
                        UUID skillId = skillsHandler.ensureSkillExists(skillName.trim());
                        JobSkillsId jobSkillsId = new JobSkillsId(jobId, skillId);
                        JobSkills jobSkills = new JobSkills(jobSkillsId);
                        jobSkillRepository.save(jobSkills);
                    }
                }
            }
        }

        // 5. Lưu job đã cập nhật
        Job updatedJob = jobRepository.save(job);

        // 6. Tạo response
        CreateJobResponseDto response = new CreateJobResponseDto(
                updatedJob.getJobId(),
                updatedJob.getTitle(),
                updatedJob.getStatus(),
                updatedJob.getImgUrl(),
                updatedJob.getCompanyId(),
                updatedJob.getApplicationCount(),
                updatedJob.getViewCount()
        );

        return ApiResponse.success("Job updated successfully!", response);
    }
}
