package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.CandidateCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CandidateCertificateRepository extends JpaRepository<CandidateCertificate, UUID> {
    List<CandidateCertificate> findByCandidateId(UUID candidateId);
}
