package com.onceClick.recruitmentService.shared.persistence.specification;

import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
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
     * Filter: by status param, or exclude 'deleted' if no status specified
     */
    public static Specification<Job> hasStatus(String status) {
        if (status != null && !status.isBlank()) {
            return (root, query, cb) -> cb.equal(root.get("status"), status);
        }
        // If no status filter, show all except deleted
        return (root, query, cb) -> cb.notEqual(root.get("status"), "deleted");
    }

    /**
     * Search: keyword in title OR description OR requirement (case-insensitive)
     */
    public static Specification<Job> hasKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return (root, query, cb) -> {
            // Tạo expression: search_vector @@ websearch_to_tsquery('simple', 'keyword')
            // Dùng cb.function với tên hàm PostgreSQL thật
            var tsquery = cb.function(
                    "websearch_to_tsquery",
                    Object.class,
                    cb.literal("simple"),
                    cb.literal(keyword.trim())
            );
            var rank = cb.function(
                    "ts_rank",
                    Float.class,
                    root.get("searchVector"),
                    tsquery
            );
            // ts_rank > 0 nghĩa là có match
            return cb.greaterThan(rank, 0f);
        };
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
