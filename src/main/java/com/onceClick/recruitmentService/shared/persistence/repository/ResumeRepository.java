package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    List<Resume> findByCandidateId(UUID candidateId);
    Optional<Resume> findByCandidateIdAndStatus(UUID candidateId, String status);

    // 1. Disallow all current default resumes for a candidate
    @Modifying
    @Query("""
        UPDATE Resume r
        SET isDefault = false
        WHERE r.candidateId = :candidateId AND r.isDefault = true
        """)
    void setCandidateResumesNotDefault(UUID candidateId);

    // 2. Optional: find current default resume
    @Query("SELECT r FROM Resume r WHERE r.candidateId = :candidateId AND r.isDefault = true")
    Optional<Resume> findDefaultResumeByCandidateId(UUID candidateId);

    // Reset all default của candidate
    @Modifying
    @Query("UPDATE Resume r SET r.isDefault = false WHERE r.candidateId = :candidateId")
    void resetDefaultForCandidate(@Param("candidateId") UUID candidateId);

    // Set 1 CV làm default
    @Modifying
    @Query("UPDATE Resume r SET r.isDefault = true WHERE r.resumeId = :resumeId AND r.candidateId = :candidateId")
    int setDefault(@Param("resumeId") UUID resumeId, @Param("candidateId") UUID candidateId);

    // List CVs: Default đầu tiên
    @Query("SELECT r FROM Resume r WHERE r.candidateId = :candidateId ORDER BY r.isDefault DESC, r.createdAt DESC")
    List<Resume> findByCandidateIdOrderByDefaultDesc(@Param("candidateId") UUID candidateId);

    // Default CV cho Recruiter
    Optional<Resume> findFirstByCandidateIdAndIsDefaultTrueOrderByCreatedAtDesc(@Param("candidateId") UUID candidateId);
}