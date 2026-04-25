package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.SavedJob;
import com.onceClick.recruitmentService.shared.persistence.entity.SavedJobId;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SavedJobRepository  extends JpaRepository<SavedJob, SavedJobId> {

    boolean existsByIdCandidateIdAndIdJobId(UUID candidateId, UUID jobId);

    //Xóa record (bỏ lưu job)
    @Modifying
    @Query("DELETE FROM SavedJob s WHERE s.id.candidateId= :candidateId AND s.id.jobId= :jobId")
    int deleteByCandidateIdAndJobId(
            @Param("candidateId") UUID candidateId,
            @Param("jobId") UUID jobId
    );

    //Sắp xếp theo saved_at mới lưu hiện trước
    @Query("SELECT s FROM SavedJob s WHERE s.id.candidateId = :candidateId ORDER BY s.savedAt DESC")
    Page<SavedJob> findByCandidateId(@Param("candidateId") UUID candidateId, Pageable pageable);

    long countByIdCandidateId(UUID candidateId);

    @Query("SELECT s.id.jobId FROM SavedJob s " +
            "WHERE s.id.candidateId = :candidateId AND s.id.jobId IN :jobIds")
        List<UUID> findSavedJobIdsByIds(
            @Param("candidateId") UUID candidateId,
            @Param("jobIds") List<UUID> jobIds
    );


}
