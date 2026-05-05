package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.response.JoinRequestResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.CompanyJoinRequest;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyJoinRequestRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMyJoinRequestHandler {

    private final CompanyJoinRequestRepository joinRequestRepository;
    private final CompanyRepository companyRepository;


    @Transactional(readOnly = true)
    public ApiResponse<JoinRequestResponseDto> handle(UUID employerId) {
        log.info("Employer {} fetching their latest join request", employerId);

        CompanyJoinRequest joinRequest = joinRequestRepository
                .findTopByEmployerIdOrderByCreatedAtDesc(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("JoinRequest", "employerId", employerId));

        // Lấy tên công ty để trả về FE
        Company company = companyRepository.findById(joinRequest.getCompanyId())
                .orElse(null);

        JoinRequestResponseDto dto = new JoinRequestResponseDto(
                joinRequest.getId(),
                joinRequest.getCompanyId(),
                company != null ? company.getCompanyName() : null,
                joinRequest.getEmployerId(),
                null,
                null,
                joinRequest.getMessage(),
                joinRequest.getStatus(),
                joinRequest.getCreatedAt()
        );

        return ApiResponse.success("Lấy yêu cầu gia nhập thành công!", dto);
    }

}
