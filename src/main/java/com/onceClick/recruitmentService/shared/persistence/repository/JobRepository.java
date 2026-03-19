package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByCompanyId(UUID companyId);
    List<Job> findByStatus(String status);
    List<Job> findByProvinceAndStatus(String province, String status);
    @Query("SELECT j FROM Job j WHERE j.salaryMin <= :maxSal AND j.salaryMax >= :minSal")
    List<Job> findBySalaryRange(@Param("minSal") BigDecimal minSal, @Param("maxSal") BigDecimal maxSal);
}


