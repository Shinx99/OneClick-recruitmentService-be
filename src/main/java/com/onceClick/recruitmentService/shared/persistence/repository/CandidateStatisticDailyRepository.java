package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.CandidateStatisticDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateStatisticDailyRepository
        extends JpaRepository<CandidateStatisticDaily, UUID> {
    Optional<CandidateStatisticDaily> findByCandidateIdAndDate(UUID candidateId, LocalDate date);
    @Query(value = "SELECT * FROM candidate_statistic_daily WHERE candidate_id = :candidateId AND date BETWEEN :from AND :to", nativeQuery = true)
    List<CandidateStatisticDaily> findByCandidateIdAndDateRange(@Param("candidateId") UUID candidateId,
                                                                @Param("from") LocalDate from, 
                                                                @Param("to") LocalDate to);
}

// Tương tự cho ResumeStatisticDailyRepository, JobStatisticDailyRepository, EmployerStatisticDailyRepository
