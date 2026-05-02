package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.response.GetJobDetailResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetJobDetailHandler {

    private final JobRepository jobRepository;
    private final GetJobDetailCacheHandler getJobDetailCacheHandler;

    @Transactional  //(readOnly = true)
    public ApiResponse<GetJobDetailResponseDto> getJobDetail(UUID jobId, HttpServletRequest request) {

        log.info("Fetching job detail for jobId: {}", jobId);

        // TĂNG VIEW COUNT (chống spam)
        incrementViewCount(jobId, request);

        return getJobDetailCacheHandler.getJobDetail(jobId, request);
    }



    /**
     * Tăng view count an toàn (mỗi session chỉ tăng 1 lần cho mỗi job)
     */
    private void incrementViewCount(UUID jobId, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String viewKey = "viewed_job_" + jobId;

        if (session.getAttribute(viewKey) == null) {
            jobRepository.incrementViewCount(jobId);
            session.setAttribute(viewKey, true);
            log.debug("Increased view count for job: {}", jobId);
        } else {
            log.debug("View count already increased for job: {} in this session", jobId);
        }
    }
}
