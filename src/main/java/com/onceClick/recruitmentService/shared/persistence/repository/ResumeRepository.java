package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    List<Resume> findByCandidateId(UUID candidateId);
    List<Resume> findByCandidateIdAndStatus(UUID candidateId, String status);

    // 1. Disallow all current default resumes for a candidate
    @Modifying
    @Transactional
    @Query("UPDATE Resume r SET isDefault = false WHERE r.candidateId = :candidateId AND r.isDefault = true")
    void setCandidateResumesNotDefault(UUID candidateId);

    // 2. Optional: find current default resume
    @Query("SELECT r FROM Resume r WHERE r.candidateId = :candidateId AND r.isDefault = true and r.status = 'active'")
    Optional<Resume> findDefaultResumeByCandidateId(UUID candidateId);

    // Reset all default của candidate
    @Modifying
    @Transactional
    @Query("UPDATE Resume r SET r.isDefault = false WHERE r.candidateId = :candidateId")
    void resetDefaultForCandidate(@Param("candidateId") UUID candidateId);

    // Set 1 CV làm default
    @Modifying
    @Transactional
    @Query("UPDATE Resume r SET r.isDefault = true WHERE r.resumeId = :resumeId AND r.candidateId = :candidateId")
    int setDefault(@Param("resumeId") UUID resumeId, @Param("candidateId") UUID candidateId);

    // List CVs: Default đầu tiên
    @Query("SELECT r FROM Resume r WHERE r.candidateId = :candidateId ORDER BY r.isDefault DESC, r.createdAt DESC")
    List<Resume> findByCandidateIdOrderByDefaultDesc(@Param("candidateId") UUID candidateId);

    // Default CV cho Recruiter
    Optional<Resume> findFirstByCandidateIdAndIsDefaultTrueOrderByCreatedAtDesc(@Param("candidateId") UUID candidateId);

    @Query("""
    SELECT r FROM Resume r
    WHERE r.status = 'deleted'
      AND r.deletedAt < :cutoffInstant""")
    List<Resume> findDeletedResumesOlderThan(@Param("cutoffInstant") Instant cutoffInstant);

    @Query("SELECT r.resumeId FROM Resume r " +
            "WHERE r.candidateId = :candidateId " +
            "  AND r.resumeUploadUrl LIKE CONCAT('%/', :filename)")
    Optional<UUID> findIdByCandidateIdAndFilename(
            @Param("candidateId") UUID candidateId,
            @Param("filename") String filename
    );

    // Trong ResumeRepository.java
    @Modifying
    @Transactional
    @Query("""
    UPDATE Resume r 
    SET isDefault = false 
    WHERE r.candidateId = :candidateId 
      AND r.isDefault = true 
      AND r.resumeId != :currentResumeId
""")
    void setCandidateResumesNotDefaultExcept(
            @Param("candidateId") UUID candidateId,
            @Param("currentResumeId") UUID currentResumeId
    );

    @Modifying
    @Transactional
    @Query("""
    UPDATE Resume r 
    SET r.isDefault = CASE 
        WHEN r.resumeId = :newResumeId THEN true 
        ELSE CASE WHEN r.isDefault THEN false ELSE r.isDefault END 
    END
    WHERE r.candidateId = :candidateId
""")
    int setNewDefaultAtomic(@Param("candidateId") UUID candidateId, @Param("newResumeId") UUID newResumeId);

    @Query("""
    SELECT r FROM Resume r 
    WHERE r.candidateId = :candidateId 
      AND r.status = 'active'
    ORDER BY r.isDefault DESC, r.createdAt DESC
""")
    List<Resume> findActiveCvList(@Param("candidateId") UUID candidateId);


    //---------------------------------------------------------------------------------------------------------------------------
    // SEARCH & FETCH ALL RESUMES
    //---------------------------------------------------------------------------------------------------------------------------
    @Query(value = """
    SELECT * FROM (
        SELECT *,
            CASE
                WHEN :keyword IS NOT NULL AND :keyword != ''
                THEN ts_rank(search_vector, plainto_tsquery('simple', unaccent(:keyword)))
                ELSE 0
            END AS rank
        FROM resume
        WHERE status = 'active'
        AND find_job = TRUE
        AND (:keyword IS NULL OR :keyword = ''
            OR search_vector @@ plainto_tsquery('simple', unaccent(:keyword)))
    ) AS filtered
    ORDER BY
        CASE WHEN (:keyword IS NULL OR :keyword = '')
            THEN view_count
            ELSE 0
        END DESC,
        CASE WHEN (:keyword IS NOT NULL AND :keyword != '')
            THEN rank
            ELSE 0
        END DESC,
        updated_at DESC
    """,
            countQuery = """
    SELECT COUNT(*) FROM resume
    WHERE status = 'active'
    AND find_job = TRUE
    AND (:keyword IS NULL OR :keyword = ''
        OR search_vector @@ plainto_tsquery('simple', unaccent(:keyword)))
    """,
            nativeQuery = true)
    Page<Resume> searchResumes(@Param("keyword") String keyword, Pageable pageable);



    //---------------------------------------------------------------------------------------------------------------------------
    // FETCH ACTIVE & FINDJOB = TRUE BY RESUME ID
    //---------------------------------------------------------------------------------------------------------------------------
    @Query("""
            SELECT r FROM Resume r
            WHERE resumeId = :resumeId
            AND r.status = 'active'
            AND findJob = TRUE
            """)
    Optional<Resume> findResumeByResumeId(@Param("resumeId") UUID resumeId);



    //---------------------------------------------------------------------------------------------------------------------------
    // FETCH ACTIVE & FINDJOB = TRUE BY RESUME ID
    //---------------------------------------------------------------------------------------------------------------------------
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE Resume r SET r.viewCount = r.viewCount + 1 WHERE r.resumeId = :resumeId")
    void incrementViewCount(@Param("resumeId") UUID resumeId);

}