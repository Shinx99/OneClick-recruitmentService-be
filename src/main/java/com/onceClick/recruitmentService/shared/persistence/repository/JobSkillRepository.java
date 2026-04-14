package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.JobSkills;
import com.onceClick.recruitmentService.shared.persistence.entity.JobSkillsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobSkillRepository extends JpaRepository<JobSkills, JobSkillsId> {

    List<JobSkills> findByIdJobId(UUID jobId);

}
