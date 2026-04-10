package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
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
}
