package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "candidate_experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateExperience {

    @EmbeddedId
    private CandidateExperienceId id = new CandidateExperienceId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("experienceId")
    @JoinColumn(name = "experience_id")
    private Experience experience;

    @Embeddable
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CandidateExperienceId implements Serializable {
        private UUID candidateId;
        private UUID experienceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CandidateExperience that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
