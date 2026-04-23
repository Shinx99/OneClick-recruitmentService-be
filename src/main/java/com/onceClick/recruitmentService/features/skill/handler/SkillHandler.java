package com.onceClick.recruitmentService.features.skill.handler;

import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.entity.CandidateSkill;
import com.onceClick.recruitmentService.shared.persistence.entity.Skills;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateSkillRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.SkillsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillHandler {

    private final SkillsRepository skillsRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final CandidateRepository candidateRepository;

    public void replaceSkillsForCandidate(UUID candidateId, List<String> rawSkills) {
        // 1. Chuẩn hóa và loại bỏ trùng lặp
        List<String> normalized = normalizeSkillNames(rawSkills);

        // 2. Lấy candidate entity để tạo liên kết
        Candidate candidate = candidateRepository.getReferenceById(candidateId);

        // 3. Xóa toàn bộ liên kết cũ
        candidateSkillRepository.deleteAllByCandidateId(candidateId);

        // 4. Với mỗi skill name, tìm hoặc tạo mới skill
        List<CandidateSkill> newLinks = new ArrayList<>();
        for (String skillName : normalized) {
            Skills skill = skillsRepository.findBySkillsName(skillName)
                    .orElseGet(() -> {
                        Skills newSkill = Skills.builder().skillsName(skillName).build();
                        return skillsRepository.save(newSkill);
                    });

            CandidateSkill link = CandidateSkill.builder()
                    .id(new CandidateSkill.CandidateSkillId(candidateId, skill.getSkillsId()))
                    .candidate(candidate)
                    .skill(skill)
                    .build();
            newLinks.add(link);
        }

        // 5. Lưu hàng loạt liên kết mới
        candidateSkillRepository.saveAll(newLinks);
        log.debug("Replaced {} skills for candidate {}", newLinks.size(), candidateId);
    }

    private List<String> normalizeSkillNames(List<String> rawSkills) {
        if (rawSkills == null) return Collections.emptyList();
        return rawSkills.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAllSkillNames() {
        return skillsRepository.findAll()
                .stream()
                .map(Skills::getSkillsName)
                .sorted()
                .collect(Collectors.toList());
    }
}
