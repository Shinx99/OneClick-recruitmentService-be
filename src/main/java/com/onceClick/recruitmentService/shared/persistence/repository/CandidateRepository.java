package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    Optional<Candidate> findByCccd(String cccd);

    List<Candidate> findByStatus(String status);

    List<Candidate> findByVerificationLevel(String level);

    long count();

    long countByStatus(String status);

    long countByCreatedAtBefore(Instant date);

    long countByCccdVerifiedAtIsNotNull();

    long countByStatusAndCreatedAtBetween(String status, Instant start, Instant end);

    @Query("SELECT FUNCTION('TO_CHAR', c.createdAt, 'YYYY-MM') as month, COUNT(c) " +
            "FROM Candidate c " +
            "WHERE c.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('TO_CHAR', c.createdAt, 'YYYY-MM') " +
            "ORDER BY month")
    List<Object[]> countCandidatesByMonth(@Param("startDate") Instant startDate,
                                          @Param("endDate") Instant endDate);

    @Query("SELECT c FROM Candidate c ORDER BY c.createdAt DESC")
    List<Candidate> findRecentCandidates(Pageable pageable);

    @Query(value = "SELECT * FROM candidate c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:keyword IS NULL OR c.surname ILIKE CONCAT('%', :keyword, '%') " +
            "OR c.name ILIKE CONCAT('%', :keyword, '%') " +
            "OR c.email ILIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY c.created_at DESC",
            countQuery = "SELECT count(*) FROM candidate c WHERE " +
                    "(:status IS NULL OR c.status = :status) AND " +
                    "(:keyword IS NULL OR c.surname ILIKE CONCAT('%', :keyword, '%') " +
                    "OR c.name ILIKE CONCAT('%', :keyword, '%') " +
                    "OR c.email ILIKE CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    Page<Candidate> findFilteredCandidates(@Param("status") String status,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);
}
