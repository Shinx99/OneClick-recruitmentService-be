package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.request.CreateJobRequestDto;
import com.onceClick.recruitmentService.features.job.dto.response.CreateJobResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.*;
import com.onceClick.recruitmentService.shared.persistence.handler.SkillsHandler;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobEmployerRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobSkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateJobHandler {

    private final JobRepository jobRepository;
    private final JobEmployerRepository jobEmployerRepository;
    private final EmployerRepository employerRepository;
    private final SkillsHandler skillsHandler;
    private final JobSkillRepository jobSkillRepository;

    @Transactional
    public ApiResponse<CreateJobResponseDto> createJobHandler(CreateJobRequestDto requestDto, UUID employerId) {

        // 1. Check exist employer. Employer must verify their profile first!
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Recruiter hasn't verified profile!"));

        // 2. Get company from Employer
        Company company = employer.getCompany();
        if(company == null || company.getVerifiedAt() == null){
            throw new IllegalStateException("Recruiter hasn't verified company!");
        }

        // 3. Check HR Level 1
        if(!"level1".equalsIgnoreCase(employer.getLevel())) {
            throw new IllegalStateException("Only HR Level 1 can create job!");
        }

        // 4. Save job
        Job job = Job.builder()
                .companyId(company.getCompanyId())
                .title(requestDto.title())
                .description(requestDto.description())
                .level(requestDto.level())
                .jobType(requestDto.jobType())
                .salaryMin(requestDto.salaryMin())
                .salaryMax(requestDto.salaryMax())
                .experienceMinYear(requestDto.experienceMinYear())
                .applicationDeadline(requestDto.applicationDeadline())
                .status("pending")
                .applicationCount(0) //default
                .viewCount(0)
                .createdBy(employerId)
                .build();

        Job savedJob = jobRepository.save(job);

        // 5. Create Job_Employer with role 'owner' (auto)
        JobEmployerId jobEmployerId = JobEmployerId.builder()
                .jobId(savedJob.getJobId())
                .employerId(employerId)
                .build();

        JobEmployer jobEmployer = JobEmployer.builder()
                .id(jobEmployerId)
                .accessRole("owner")
                .grantedAt(Instant.now())
                .build();

        jobEmployerRepository.save(jobEmployer);

        // 6. Handle Skills
        if((requestDto.skillIds() != null && !requestDto.skillIds().isEmpty()) ||
                (requestDto.skillNames() != null && !requestDto.skillNames().isEmpty())){

            // A. Existing skills - Check with SkillIds
            if(requestDto.skillIds() != null){
                for(UUID skillsId : requestDto.skillIds()){
                    JobSkillsId jobSkillsId = new JobSkillsId(savedJob.getJobId(), skillsId);
                    JobSkills jobSkills = new JobSkills(jobSkillsId);
                    jobSkillRepository.save(jobSkills);
                }
            }

            // B. Create new one - Save with skillNames
            if(requestDto.skillNames() != null){
                for(String skillName : requestDto.skillNames()) {
                    if(skillName != null && !skillName.trim().isEmpty()){
                        UUID skillId = skillsHandler.ensureSkillExists(skillName.trim());
                        JobSkillsId jobSkillsId = new JobSkillsId(savedJob.getJobId(), skillId);
                        JobSkills jobSkills = new JobSkills(jobSkillsId);
                        jobSkillRepository.save(jobSkills);
                    }
                }
            }
        }

        // 5. Return Response
        CreateJobResponseDto response = new CreateJobResponseDto(
                savedJob.getJobId(),
                savedJob.getTitle(),
                savedJob.getStatus(),
                company.getCompanyId(),
                savedJob.getApplicationCount(),
                savedJob.getViewCount()
        );

        return ApiResponse.success("Job created successfully!", response);
    }
}
