package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillsRepository extends JpaRepository<Skills, UUID> {
    Optional<Skills> findBySkillsName(String name);
    List<Skills> findBySkillsNameContainingIgnoreCase(String keyword);

    // Search for autocomplete
    @Query(value = "SELECT * FROM skills WHERE LOWER(skill_name) LIKE LOWER(:query)", nativeQuery = true)
    List<Skills> search(@Param("query") String query);
}


