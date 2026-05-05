package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.response.TopJobsResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetTopJobsHandler {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<TopJobsResponseDto>> getTop6ViewedJobs() {
        List<Job> topJobs = jobRepository.findTopJobsByViewCount(PageRequest.of(0, 6));
        if (topJobs.isEmpty()) {
            // Nếu trống, trả về phản hồi thành công nhưng với thông báo "Không có dữ liệu" và một danh sách rỗng (List.of()).
            return ApiResponse.success("Không có dữ liệu", List.of());
        }

        List<UUID> companyIds = topJobs.stream().map(Job::getCompanyId).distinct().toList();
        Map<UUID, Company> companyMap = companyRepository.findAllByCompanyIdIn(companyIds)
                .stream()
                .collect(Collectors.toMap(Company::getCompanyId, c -> c));

        List<TopJobsResponseDto> responseDtos = topJobs.stream()
                .map(job -> {
                    Company company = companyMap.get(job.getCompanyId());

                    return new TopJobsResponseDto(
                            job.getJobId(),
                            job.getTitle(),
                            job.getProvince(),
                            job.getSalaryMin(),
                            job.getSalaryMax(),
                            job.getViewCount(),
                            job.getJobType(),
                            job.getStatus(),
                            company != null ? company.getCompanyId() : null,
                            company != null ? company.getCompanyName() : null,
                            company != null ? company.getLogoUrl() : null
                    );
                })
                .toList();

        log.info("Đã lấy Top 6 Jobs có lượt xem cao nhất.");
        return ApiResponse.success("Lấy Top 6 công việc nổi bật thành công", responseDtos);
    }


}
