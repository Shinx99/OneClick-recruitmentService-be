package com.onceClick.recruitmentService.features.savedJob.handler;

import com.onceClick.recruitmentService.features.savedJob.dto.response.SavedJobStatusDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class UnsaveJobHandler {
    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;

    @Transactional
    public ApiResponse<SavedJobStatusDto> unsaveJob(UUID candidateId, UUID jobId) {

        int deletedRows = savedJobRepository
                .deleteByCandidateIdAndJobId(candidateId, jobId);

        if (deletedRows > 0) {
            jobRepository.decrementSaveCount(jobId);
            log.info("Candidate {} đã bỏ lưu job {}", candidateId, jobId);
        } else {
            log.info("Candidate {} chưa lưu job {} → không cần xóa", candidateId, jobId);
        }

        return ApiResponse.success(
                "Bỏ lưu công việc thành công",
                new SavedJobStatusDto(jobId, false)
        );
    }



}
