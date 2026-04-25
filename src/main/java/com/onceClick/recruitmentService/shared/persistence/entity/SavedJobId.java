package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;
// Mảnh nhúng
// @Embeddable: Báo cho JPA biết class này không phải là 1 bảng riêng,  nhúng vào làm Khóa Chính cho bảng khác.
// implements Serializable: Bắt buộc đối với class làm Khóa Chính kép trong Java.
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobId implements Serializable {

    @Column(name = "candidate_id")
    private UUID candidateId;

    @Column(name = "job_id")
    private UUID jobId;
}