package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.CandidateExperience;
import com.onceClick.recruitmentService.shared.persistence.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CandidateExperienceRepository extends JpaRepository<CandidateExperience, CandidateExperience.CandidateExperienceId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM CandidateExperience ce WHERE ce.id.candidateId = :candidateId")
    void deleteAllByCandidateId(@Param("candidateId") UUID candidateId);

    @Query("SELECT ce.experience FROM CandidateExperience ce WHERE ce.id.candidateId = :candidateId")
    List<Experience> findAllExperiencesByCandidateId(@Param("candidateId") UUID candidateId);
}
