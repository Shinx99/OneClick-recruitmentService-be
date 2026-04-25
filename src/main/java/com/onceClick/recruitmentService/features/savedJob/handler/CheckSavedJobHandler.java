package com.onceClick.recruitmentService.features.savedJob.handler;
import com.onceClick.recruitmentService.features.savedJob.dto.response.SavedJobStatusDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckSavedJobHandler {

    private final SavedJobRepository savedJobRepository;

    public ApiResponse<SavedJobStatusDto> isSaved(UUID candidateId, UUID jobId){

        boolean saved = savedJobRepository.existsByIdCandidateIdAndIdJobId(candidateId, jobId);
        return ApiResponse.success("Kiểm tra trạng thái lưu thành công",
                new SavedJobStatusDto(jobId, saved));

    }

}
