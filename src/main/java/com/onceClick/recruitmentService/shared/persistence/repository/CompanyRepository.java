package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByTaxCode(String taxCode);

    Optional<Company> findByCompanyName(String name);

    List<Company> findByStatus(String status);

    List<Company> findByProvinceCode(String provinceCode);
}
