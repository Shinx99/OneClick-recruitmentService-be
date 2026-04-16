package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skills {
    
    @Id
    @GeneratedValue
    @Column(name = "skills_id")
    private UUID skillsId;
    
    @Column(name = "skills_name", unique = true, nullable = false, length = 255)
    @NotBlank
    private String skillsName;

    public Skills(String skillsName) {
        this.skillsName = skillsName;
    }

/*    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;*/

}
