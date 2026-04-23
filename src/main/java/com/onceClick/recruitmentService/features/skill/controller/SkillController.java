package com.onceClick.recruitmentService.features.skill.controller;

import com.onceClick.recruitmentService.features.skill.handler.SkillHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruitment/skills")
public class SkillController {

    private final SkillHandler skillHandler;

    @GetMapping("/all")
    public ApiResponse<List<String>> getAllSkills() {
        List<String> skills = skillHandler.getAllSkillNames();
        return ApiResponse.success(skills);
    }
}
