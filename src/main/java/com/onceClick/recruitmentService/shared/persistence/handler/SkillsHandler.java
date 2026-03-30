package com.onceClick.recruitmentService.shared.persistence.handler;

import com.onceClick.recruitmentService.shared.persistence.entity.Skills;
import com.onceClick.recruitmentService.shared.persistence.repository.SkillsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillsHandler {

    private final SkillsRepository skillsRepository;

    public UUID ensureSkillExists(String skillName){
        return skillsRepository.findBySkillsNameContainingIgnoreCase(skillName)
                .stream()
                .findFirst()
                .map(Skills::getSkillsId)
                .orElseGet(() -> {
                    Skills newSkill = new Skills(skillName);
                    return skillsRepository.save(newSkill).getSkillsId();
                });
    }

    public List<Skills> searchSkills(String query){
        return skillsRepository.search("%" + query.toLowerCase() + "%");
    }

}
