package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.response.JoinRequestResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.CompanyJoinRequest;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyJoinRequestRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetJoinRequestsHandler {

    private final CompanyJoinRequestRepository joinRequestRepository;
    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<JoinRequestResponseDto>> getPendingRequests(UUID ownerId, Pageable pageable) {
        log.info("Owner {} fetching pending join requests", ownerId);

        // Why: find the company this owner created — owner is identified by company.createdBy
        Employer owner = employerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "employerId", ownerId));

        Company company = owner.getCompany();
        if (company == null) {
            throw new IllegalStateException("Bạn chưa có công ty!");
        }

        if (!company.getCreatedBy().equals(ownerId)) {
            throw new IllegalStateException("Bạn không phải người tạo công ty này!");
        }

        Page<CompanyJoinRequest> requestPage = joinRequestRepository
                .findByCompanyIdAndStatus(company.getCompanyId(), "PENDING", pageable);

        // Why: batch fetch employer data to avoid N+1 queries
        List<UUID> employerIds = requestPage.getContent().stream()
                .map(CompanyJoinRequest::getEmployerId)
                .distinct()
                .toList();

        Map<UUID, Employer> employerMap = employerRepository.findAllById(employerIds).stream()
                .collect(Collectors.toMap(Employer::getEmployerId, e -> e));

        Page<JoinRequestResponseDto> dtoPage = requestPage.map(req -> {
            Employer applicant = employerMap.get(req.getEmployerId());
            return new JoinRequestResponseDto(
                    req.getId(),
                    req.getCompanyId(),
                    company.getCompanyName(),
                    req.getEmployerId(),
                    applicant != null ? applicant.getName() : null,
                    applicant != null ? applicant.getEmail() : null,
                    req.getMessage(),
                    req.getStatus(),
                    req.getCreatedAt()
            );
        });

        PageResponse<JoinRequestResponseDto> pageResponse = PageResponse.from(dtoPage);
        return ApiResponse.success("Lấy danh sách yêu cầu gia nhập thành công!", pageResponse);
    }
}
