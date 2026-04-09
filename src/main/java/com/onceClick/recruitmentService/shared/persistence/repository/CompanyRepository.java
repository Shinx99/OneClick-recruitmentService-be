package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
//    Optional<Company> findByTaxCode(String taxCode);
//    Optional<Company> findByCompanyName(String name);
//    List<Company> findByStatus(String status);
//    List<Company> findByProvinceCode(String provinceCode);

// Query tìm danh sách company theo điều kiện search/filter
@Query(value = """
        SELECT c FROM Company c
        WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:provinceCode IS NULL OR :provinceCode = '' OR c.provinceCode = :provinceCode)
          AND (:industry IS NULL OR :industry = '' OR LOWER(c.industry) LIKE LOWER(CONCAT('%', CAST(:industry AS string), '%')))
          AND (:status IS NULL OR :status = '' OR c.status = :status)
        """,
        countQuery = """
        SELECT COUNT(c) FROM Company c
        WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:provinceCode IS NULL OR :provinceCode = '' OR c.provinceCode = :provinceCode)
          AND (:industry IS NULL OR :industry = '' OR LOWER(c.industry) LIKE LOWER(CONCAT('%', CAST(:industry AS string), '%')))
          AND (:status IS NULL OR :status = '' OR c.status = :status)
        """)
Page<Company> searchCompanies(
        @Param("keyword") String keyword,
        @Param("provinceCode") String provinceCode,
        @Param("industry") String industry,
        @Param("status") String status,
        Pageable pageable
);

    @Query("""
        SELECT c FROM Company c
        WHERE c.verifiedAt IS NOT NULL
        ORDER BY c.verifiedAt DESC
    """)
    Page<Company> findTopVerifiedCompanies(Pageable pageable);
}
