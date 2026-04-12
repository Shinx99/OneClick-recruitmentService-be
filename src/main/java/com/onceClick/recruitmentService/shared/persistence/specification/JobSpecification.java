package com.onceClick.recruitmentService.shared.persistence.specification;

import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class JobSpecification {

    private JobSpecification() {
    }

    /**
     * Filter: status = 'active' (always applied)
     */
    public static Specification<Job> isActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), "active");
    }

    /**
     * Search: keyword in title OR description OR requirement (case-insensitive)
     */
    public static Specification<Job> hasKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String pattern = "%" + keyword.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern),
                cb.like(cb.lower(root.get("requirement")), pattern)
        );
    }

    /**
     * Filter: province exact match
     */
    public static Specification<Job> hasProvince(String province) {
        if (province == null || province.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("province"), province);
    }

    /**
     * Filter: level exact match (Senior, Middle, Junior, Fresher)
     */
    public static Specification<Job> hasLevel(String level) {
        if (level == null || level.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("level"), level);
    }

    /**
     * Filter: jobType exact match (Full-time, Part-time, Internship, Contract)
     */
    public static Specification<Job> hasJobType(String jobType) {
        if (jobType == null || jobType.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("jobType"), jobType);
    }

    /**
     * Filter: job salaryMax >= salaryMin param
     * (job offers at least this minimum salary)
     */
    public static Specification<Job> hasSalaryMin(BigDecimal salaryMin) {
        if (salaryMin == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("salaryMax"), salaryMin);
    }

    /**
     * Filter: job salaryMin <= salaryMax param
     * (job salary doesn't exceed this maximum)
     */
    public static Specification<Job> hasSalaryMax(BigDecimal salaryMax) {
        if (salaryMax == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("salaryMin"), salaryMax);
    }

    /**
     * Filter: experienceMinYear <= experienceMax param
     * (job requires at most this many years of experience)
     */
    public static Specification<Job> hasExperienceMax(BigDecimal experienceMax) {
        if (experienceMax == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("experienceMinYear"), experienceMax);
    }
}
