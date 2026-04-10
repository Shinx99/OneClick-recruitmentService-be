package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
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
}
