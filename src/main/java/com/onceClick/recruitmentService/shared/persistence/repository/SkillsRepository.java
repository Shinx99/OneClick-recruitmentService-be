package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillsRepository extends JpaRepository<Skills, UUID> {
    Optional<Skills> findBySkillsName(String name);
    List<Skills> findBySkillsNameContainingIgnoreCase(String keyword);
}


