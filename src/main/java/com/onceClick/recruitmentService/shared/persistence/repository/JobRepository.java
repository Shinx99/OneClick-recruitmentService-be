package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.entity.JobSkills;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID>, JpaSpecificationExecutor<Job> {
    Page<Job> findByCompanyId(UUID companyId, Pageable pageable);
    List<Job> findByStatus(String status);
    List<Job> findByProvinceAndStatus(String province, String status);
    @Query("SELECT j FROM Job j WHERE j.salaryMin <= :maxSal AND j.salaryMax >= :minSal")
    List<Job> findBySalaryRange(@Param("minSal") BigDecimal minSal, @Param("maxSal") BigDecimal maxSal);

    // Dashboard queries
    long count();

    long countByStatus(String status);

    long countByCreatedAtBefore(Instant date);

    long countByStatusAndCreatedAtBetween(String status, Instant start, Instant end);

    @Query("SELECT FUNCTION('TO_CHAR', j.createdAt, 'YYYY-MM') as month, COUNT(j) " +
            "FROM Job j " +
            "WHERE j.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('TO_CHAR', j.createdAt, 'YYYY-MM') " +
            "ORDER BY month")
    List<Object[]> countJobsByMonth(@Param("startDate") Instant startDate,
                                    @Param("endDate") Instant endDate);

    @Query("SELECT j FROM Job j ORDER BY j.createdAt DESC")
    List<Job> findRecentJobs(Pageable pageable);

    // Related Jobs: tìm các job có chung ít nhất 1 skill với job hiện tại
    @Query("""
        SELECT j FROM Job j
        WHERE j.jobId IN (
            SELECT js2.id.jobId FROM JobSkills js2
            WHERE js2.id.skillsId IN (
                SELECT js1.id.skillsId FROM JobSkills js1
                WHERE js1.id.jobId = :jobId
            )
            AND js2.id.jobId <> :jobId
        )
        AND j.status = 'active'
        """)
    Page<Job> findRelatedJobsBySkills(@Param("jobId") UUID jobId, Pageable pageable);

    // Related Jobs: tìm các job cùng ngành (majorPreferred) với job hiện tại
    @Query("""
        SELECT j FROM Job j
        WHERE j.majorPreferred = :majorPreferred
        AND j.jobId <> :jobId
        AND j.status = 'active'
        ORDER BY j.createdAt DESC
        """)
    Page<Job> findRelatedJobsByMajor(
            @Param("majorPreferred") String majorPreferred,
            @Param("jobId") UUID jobId,
            Pageable pageable
    );

    // Fallback: lấy các job active mới nhất, loại trừ job hiện tại
    @Query("""
        SELECT j FROM Job j
        WHERE j.jobId <> :jobId
        AND j.status = 'active'
        AND j.applicationDeadline > CURRENT_TIMESTAMP
        ORDER BY j.createdAt DESC
        """)
    Page<Job> findFallbackRelatedJobs(@Param("jobId") UUID jobId, Pageable pageable);

    // Related Jobs (không phân trang): trả về TẤT CẢ jobs cùng ngành
    @Query("""
        SELECT j FROM Job j
        WHERE j.majorPreferred = :majorPreferred
        AND j.jobId <> :jobId
        AND j.status = 'active'
        AND j.applicationDeadline > CURRENT_TIMESTAMP
        ORDER BY j.createdAt DESC
        """)
    List<Job> findAllRelatedJobsByMajor(
            @Param("majorPreferred") String majorPreferred,
            @Param("jobId") UUID jobId
    );

    // Fallback (không phân trang): trả về TẤT CẢ jobs active mới nhất (ĐÃ THÊM LỌC HẠN)
    @Query("""
        SELECT j FROM Job j
        WHERE j.jobId <> :jobId
        AND j.status = 'active'
        AND j.applicationDeadline > CURRENT_TIMESTAMP
        ORDER BY j.createdAt DESC
        """)
    List<Job> findAllFallbackRelatedJobs(@Param("jobId") UUID jobId);

    @Transactional
    @Modifying
    @Query("UPDATE Job j SET j.saveCount = COALESCE(j.saveCount, 0) + 1 WHERE j.jobId = :jobId")
    int incrementSaveCount(@Param("jobId") UUID jobId);

    @Transactional
    @Modifying
    @Query("UPDATE Job j SET j.saveCount = CASE WHEN COALESCE(j.saveCount, 0) > 0 THEN j.saveCount - 1 ELSE 0 END WHERE j.jobId = :jobId")
    int decrementSaveCount(@Param("jobId") UUID jobId);

    List<Job> findByCreatedBy(UUID employerId);

    // Why: query through job_employer junction table to find jobs owned by a specific employer
    @Query("""
        SELECT j FROM Job j
        WHERE j.jobId IN (
            SELECT je.id.jobId FROM JobEmployer je
            WHERE je.id.employerId = :employerId
        )
        ORDER BY j.createdAt DESC
        """)
    Page<Job> findJobsByEmployerId(@Param("employerId") UUID employerId, Pageable pageable);

    // Xử lý cho lượt ứng tuyển và lượt view
    @Modifying
    @Transactional
    @Query("UPDATE Job j SET j.applicationCount = j.applicationCount + 1 WHERE j.jobId = :jobId")
    void incrementApplicationCount(@Param("jobId") UUID jobId);

    @Modifying
    @Transactional
    @Query("UPDATE Job j SET j.applicationCount = j.applicationCount - 1 WHERE j.jobId = :jobId AND j.applicationCount > 0")
    void decrementApplicationCount(@Param("jobId") UUID jobId);

    @Modifying
    @Transactional
    @Query("UPDATE Job j SET j.viewCount = j.viewCount + 1 WHERE j.jobId = :jobId")
    void incrementViewCount(@Param("jobId") UUID jobId);

    @Query("SELECT j FROM Job j WHERE j.status = 'active' ORDER BY j.viewCount DESC, j.createdAt DESC")
    List<Job> findTopJobsByViewCount(org.springframework.data.domain.Pageable pageable);

    // JobRepository.java - Dùng native query
    @Query(value = """
    SELECT * FROM job j
    WHERE j.job_id IN (
        SELECT je.job_id FROM job_employer je 
        WHERE je.employer_id = :employerId
    )
    AND (:keyword IS NULL OR j.title ILIKE CONCAT('%', :keyword, '%'))
    AND (:status IS NULL OR j.status = :status)
    ORDER BY j.created_at DESC
    LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<Job> findByEmployerIdAndFiltersNative(
            @Param("employerId") UUID employerId,
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
    SELECT COUNT(*) FROM job j
    WHERE j.job_id IN (
        SELECT je.job_id FROM job_employer je 
        WHERE je.employer_id = :employerId
    )
    AND (:keyword IS NULL OR j.title ILIKE CONCAT('%', :keyword, '%'))
    AND (:status IS NULL OR j.status = :status)
    """, nativeQuery = true)
    long countByEmployerIdAndFilters(
            @Param("employerId") UUID employerId,
            @Param("keyword") String keyword,
            @Param("status") String status
    );
}

