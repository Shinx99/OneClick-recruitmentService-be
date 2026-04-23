package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.CandidateEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CandidateEducationRepository extends JpaRepository<CandidateEducation, UUID> {
    List<CandidateEducation> findByCandidateId(UUID candidateId);
    List<CandidateEducation> findByCandidateIdAndIsCurrentTrue(UUID candidateId);
    List<CandidateEducation> findAllByCandidateCandidateId(UUID candidateId);
    void deleteByCandidateCandidateIdAndEducationId(UUID candidateId, UUID educationId);
}