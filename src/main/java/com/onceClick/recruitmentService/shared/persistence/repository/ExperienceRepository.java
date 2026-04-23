package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.CandidateExperience;
import com.onceClick.recruitmentService.shared.persistence.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExperienceRepository extends JpaRepository<Experience, UUID> {
    List<Experience> findByCompany_CompanyId(UUID companyId);
}