package com.onceClick.recruitmentService.features.savedJob.handler;

import com.onceClick.recruitmentService.features.savedJob.dto.response.SavedJobStatusDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.SavedJob;
import com.onceClick.recruitmentService.shared.persistence.entity.SavedJobId;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveJobHandler {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;

    public ApiResponse<SavedJobStatusDto> saveJob(UUID candidateId, UUID jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Không tìm thấy công việc với ID: " + jobId);
        }

        boolean alreadySaved = savedJobRepository.existsByIdCandidateIdAndIdJobId(candidateId, jobId);
        if (alreadySaved) {
            log.info("Candidate {} đã lưu job {} trước đó → skip", candidateId, jobId);
            return ApiResponse.success(
                    "Công việc đã được lưu trước đó",
                    new SavedJobStatusDto(jobId, true)
            );
        }
        SavedJobId savedJobId = SavedJobId.builder()
                .candidateId(candidateId)
                .jobId(jobId)
                .build();

        SavedJob savedJob = SavedJob.builder()
                .id(savedJobId)
                .build();
        savedJobRepository.save(savedJob);

        jobRepository.incrementSaveCount(jobId);
        log.info("Candidate {} đã lưu job {}", candidateId, jobId);

        return ApiResponse.success(
                "Lưu công việc thành công",
                new SavedJobStatusDto(jobId, true)
        );
    }
}
