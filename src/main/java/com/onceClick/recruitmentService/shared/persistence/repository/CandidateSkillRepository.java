package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkill.CandidateSkillId> {
    @Modifying
    @Transactional
    @Query("DELETE FROM CandidateSkill cs WHERE cs.id.candidateId = :candidateId")
    void deleteAllByCandidateId(@Param("candidateId") UUID candidateId);

    @Query("SELECT cs.skill.skillsName FROM CandidateSkill cs WHERE cs.id.candidateId = :candidateId ORDER BY cs.skill.skillsName")
    List<String> findSkillNamesByCandidateId(@Param("candidateId") UUID candidateId);
}
