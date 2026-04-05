package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT j FROM Job j WHERE " +
            "(CAST(:keyword AS text) IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%'))) AND " +
            "(CAST(:province AS text) IS NULL OR j.province = CAST(:province AS text)) AND " +
            "(CAST(:level AS text) IS NULL OR j.level = CAST(:level AS text)) AND " +
            "j.status = 'active'")
    Page<Job> searchAndFilterJobs(
            @Param("keyword") String keyword,
            @Param("province") String province,
            @Param("level") String level,
            Pageable pageable
    );
}


