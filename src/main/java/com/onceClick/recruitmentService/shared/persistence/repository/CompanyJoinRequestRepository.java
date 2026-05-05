package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.CompanyJoinRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyJoinRequestRepository extends JpaRepository<CompanyJoinRequest, UUID> {

    Page<CompanyJoinRequest> findByCompanyIdAndStatus(UUID companyId, String status, Pageable pageable);

    List<CompanyJoinRequest> findByEmployerIdAndStatus(UUID employerId, String status);

    boolean existsByCompanyIdAndEmployerIdAndStatus(UUID companyId, UUID employerId, String status);

    Optional<CompanyJoinRequest> findTopByEmployerIdOrderByCreatedAtDesc(UUID employerId);
}
