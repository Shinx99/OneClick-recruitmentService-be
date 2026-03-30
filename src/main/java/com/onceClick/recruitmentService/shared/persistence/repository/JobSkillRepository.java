package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.JobSkills;
import com.onceClick.recruitmentService.shared.persistence.entity.JobSkillsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSkillRepository extends JpaRepository<JobSkills, JobSkillsId> {

}
