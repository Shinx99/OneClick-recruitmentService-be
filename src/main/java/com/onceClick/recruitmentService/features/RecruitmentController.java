/*
package com.onceClick.recruitmentService.features;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/recruitment")
public class RecruitmentController {

    // Public - không cần token
    @GetMapping("/jobs")
    public Map<String, Object> publicJobs() {
        return Map.of("jobs", List.of("Job1", "Job2"), "message", "Public OK");
    }

    // Protected - cần valid token
    @GetMapping("/protected")
    public Map<String, Object> protectedJobs(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();
        String userId = Optional.ofNullable(jwt.getClaimAsString("accountId")).orElse("unknown");
        @SuppressWarnings("unchecked")
        List<String> roles = jwt.getClaimAsStringList("roles");

        return Map.of(
                "jobs", List.of("Premium Job1", "Premium Job2"),
                "userId", userId,
                "roles", roles != null ? roles : List.of()
        );
    }

    // Admin only
    @GetMapping("/admin")
    public Map<String, Object> adminJobs(Authentication auth) {
        return Map.of("adminJobs", "Secret Jobs", "roles", auth.getAuthorities());
    }
}
*/
