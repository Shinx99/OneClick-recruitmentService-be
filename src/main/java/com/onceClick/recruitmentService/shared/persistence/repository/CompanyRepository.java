package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    // Search/filter companies
    @Query(value = """
            SELECT c FROM Company c
            WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
              AND (:provinceCode IS NULL OR :provinceCode = '' OR c.provinceCode = :provinceCode)
              AND (:industry IS NULL OR :industry = '' OR LOWER(c.industry) LIKE LOWER(CONCAT('%', CAST(:industry AS string), '%')))
              AND (:sizeRange IS NULL OR :sizeRange = '' OR c.sizeRange = :sizeRange)
              AND (:status IS NULL OR :status = '' OR c.status = :status)
            """,
            countQuery = """
            SELECT COUNT(c) FROM Company c
            WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
              AND (:provinceCode IS NULL OR :provinceCode = '' OR c.provinceCode = :provinceCode)
              AND (:industry IS NULL OR :industry = '' OR LOWER(c.industry) LIKE LOWER(CONCAT('%', CAST(:industry AS string), '%')))
              AND (:sizeRange IS NULL OR :sizeRange = '' OR c.sizeRange = :sizeRange)
              AND (:status IS NULL OR :status = '' OR c.status = :status)
            """)
    Page<Company> searchCompanies(
            @Param("keyword") String keyword,
            @Param("provinceCode") String provinceCode,
            @Param("industry") String industry,
            @Param("sizeRange") String sizeRange,
            @Param("status") String status,
            Pageable pageable
    );


    @Query("""
        SELECT c FROM Company c
        WHERE c.sizeRange IS NOT NULL AND c.sizeRange != ''
        ORDER BY CASE c.sizeRange
            WHEN '1-50' THEN 1
            WHEN '50-150' THEN 2
            WHEN '150-500' THEN 3
            WHEN '500-1000' THEN 4
            WHEN '1000+' THEN 5
            ELSE 0
        END DESC, c.companyName ASC
    """)
    Page<Company> findTopCompaniesBySize(Pageable pageable);

    // Filter
    @Query("SELECT DISTINCT c.industry FROM Company c WHERE c.industry IS NOT NULL")
    List<String> findDistinctIndustries();


    @Query("SELECT DISTINCT c.sizeRange FROM Company c WHERE c.sizeRange IS NOT NULL")
    List<String> findDistinctSizeRanges();

    @Query("SELECT DISTINCT c.provinceCode FROM Company c WHERE c.provinceCode IS NOT NULL")
    List<String> findDistinctProvinces();





    // Dashboard queries
    long count();

    long countByStatus(String status);

    long countByCreatedAtBefore(Instant date);

    long countByVerifiedAtIsNotNull();

    long countByStatusAndCreatedAtBetween(String status, Instant start, Instant end);

    @Query("SELECT FUNCTION('TO_CHAR', c.createdAt, 'YYYY-MM') as month, COUNT(c) " +
            "FROM Company c " +
            "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('TO_CHAR', c.createdAt, 'YYYY-MM') " +
            "ORDER BY month")
    List<Object[]> countCompaniesByMonth(@Param("startDate") Instant startDate,
                                         @Param("endDate") Instant endDate);

    @Query("SELECT c FROM Company c ORDER BY c.createdAt DESC")
    List<Company> findRecentCompanies(Pageable pageable);

    // Batch lookup for Job -> Company mapping
    List<Company> findByProvinceCode(String provinceCode);

    List<Company> findAllByCompanyIdIn(List<UUID> companyIds);
}
