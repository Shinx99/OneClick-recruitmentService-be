package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployerRepository extends JpaRepository<Employer, UUID> {

    long countByStatus(String status);

    long countByCreatedAtBefore(Instant date);

    long countByStatusAndCreatedAtBetween(String status, Instant start, Instant end);

    @Query("SELECT FUNCTION('TO_CHAR', e.createdAt, 'YYYY-MM') as month, COUNT(e) " +
            "FROM Employer e " +
            "WHERE e.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('TO_CHAR', e.createdAt, 'YYYY-MM') " +
            "ORDER BY month")
    List<Object[]> countEmployersByMonth(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT e FROM Employer e ORDER BY e.createdAt DESC")
    List<Employer> findRecentEmployers(Pageable pageable);

    @Query(value = "SELECT e.* FROM employer e " +
            "JOIN company c ON e.company_id = c.company_id " +
            "WHERE (:status IS NULL OR e.status = :status) AND " +
            "(:keyword IS NULL OR e.surname ILIKE CONCAT('%', :keyword, '%') " +
            "OR e.name ILIKE CONCAT('%', :keyword, '%') " +
            "OR e.email ILIKE CONCAT('%', :keyword, '%') " +
            "OR c.company_name ILIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY e.created_at DESC",
            countQuery = "SELECT count(*) FROM employer e " +
                    "JOIN company c ON e.company_id = c.company_id " +
                    "WHERE (:status IS NULL OR e.status = :status) AND " +
                    "(:keyword IS NULL OR e.surname ILIKE CONCAT('%', :keyword, '%') " +
                    "OR e.name ILIKE CONCAT('%', :keyword, '%') " +
                    "OR e.email ILIKE CONCAT('%', :keyword, '%') " +
                    "OR c.company_name ILIKE CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    Page<Employer> findFilteredEmployers(@Param("status") String status,
                                         @Param("keyword") String keyword,
                                         Pageable pageable);


    @Query("""
        SELECT e FROM Employer e 
        WHERE e.company.companyId = :companyId 
        AND e.status = 'active'
        AND e.company.status = 'active'
    """)
    List<Employer> findActiveEmployersByCompanyId(@Param("companyId") UUID companyId);

    // Dùng trong reject(): unlink employer khỏi company trước khi xóa
    Optional<Employer> findByCompanyCompanyId(UUID companyId);

}
