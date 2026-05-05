package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.request.JoinCompanyRequestDto;
import com.onceClick.recruitmentService.features.company.dto.response.JoinRequestResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.CompanyJoinRequest;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.entity.Notification;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyJoinRequestRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendJoinRequestHandler {

    private final CompanyJoinRequestRepository joinRequestRepository;
    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public ApiResponse<JoinRequestResponseDto> sendJoinRequest(UUID employerId, UUID companyId, JoinCompanyRequestDto requestDto) {
        log.info("Employer {} sending join request to company {}", employerId, companyId);

        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "employerId", employerId));

        // Why: employer already in a company cannot join another
        if (employer.getCompany() != null) {
            throw new IllegalStateException("Bạn đã thuộc một công ty, không thể gửi yêu cầu gia nhập!");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "companyId", companyId));

        // Why: partial unique index in DB also prevents this, but early check gives better error message
        if (joinRequestRepository.existsByCompanyIdAndEmployerIdAndStatus(companyId, employerId, "PENDING")) {
            throw new IllegalStateException("Bạn đã gửi yêu cầu gia nhập công ty này, vui lòng chờ duyệt!");
        }

        CompanyJoinRequest joinRequest = CompanyJoinRequest.builder()
                .companyId(companyId)
                .employerId(employerId)
                .message(requestDto != null ? requestDto.getMessage() : null)
                .build();

        CompanyJoinRequest saved = joinRequestRepository.save(joinRequest);

        // Why: only notify if company has an owner — seed data companies may have createdBy = null
//        if (company.getCreatedBy() != null) {
//            Notification notification = Notification.builder()
//                    .userId(company.getCreatedBy())
//                    .type("JOIN_REQUEST")
//                    .title("Yêu cầu gia nhập công ty")
//                    .content(String.format("%s (%s) muốn gia nhập công ty %s",
//                            employer.getName() != null ? employer.getName() : "Employer",
//                            employer.getEmail(),
//                            company.getCompanyName()))
//                    .isRead(false)
//                    .build();
//            notificationRepository.save(notification);
//        } else {
//            log.warn("Company {} has no owner (createdBy is null), skipping notification", companyId);
//        }

        JoinRequestResponseDto responseDto = new JoinRequestResponseDto(
                saved.getId(),
                saved.getCompanyId(),
                company.getCompanyName(),
                saved.getEmployerId(),
                employer.getName(),
                employer.getEmail(),
                saved.getMessage(),
                saved.getStatus(),
                saved.getCreatedAt()
        );

        return ApiResponse.success("Gửi yêu cầu gia nhập thành công! Vui lòng chờ duyệt.", responseDto);
    }
}
