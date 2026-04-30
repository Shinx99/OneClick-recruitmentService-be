package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.response.JoinRequestResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ForbiddenException;
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

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewJoinRequestHandler {

    private final CompanyJoinRequestRepository joinRequestRepository;
    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public ApiResponse<JoinRequestResponseDto> approve(UUID requestId, UUID reviewerId) {
        log.info("Reviewer {} approving join request {}", reviewerId, requestId);

        CompanyJoinRequest joinRequest = findAndValidate(requestId, reviewerId);

        Company company = companyRepository.findById(joinRequest.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "companyId", joinRequest.getCompanyId()));

        Employer applicant = employerRepository.findById(joinRequest.getEmployerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "employerId", joinRequest.getEmployerId()));

        joinRequest.setStatus("APPROVED");
        joinRequest.setReviewedBy(reviewerId);
        joinRequest.setReviewedAt(Instant.now());
        joinRequestRepository.save(joinRequest);

        // Why: this is the actual "join" — linking employer to company via the ManyToOne FK
        applicant.setCompany(company);
        employerRepository.save(applicant);

        Notification notification = Notification.builder()
                .userId(joinRequest.getEmployerId())
                .type("JOIN_REQUEST_APPROVED")
                .title("Yêu cầu gia nhập được chấp nhận")
                .content(String.format("Yêu cầu gia nhập công ty %s đã được chấp nhận! Bạn giờ là thành viên của công ty.",
                        company.getCompanyName()))
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        return ApiResponse.success("Đã duyệt yêu cầu gia nhập!", buildResponse(joinRequest, company, applicant));
    }

    @Transactional
    public ApiResponse<JoinRequestResponseDto> reject(UUID requestId, UUID reviewerId) {
        log.info("Reviewer {} rejecting join request {}", reviewerId, requestId);

        CompanyJoinRequest joinRequest = findAndValidate(requestId, reviewerId);

        Company company = companyRepository.findById(joinRequest.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "companyId", joinRequest.getCompanyId()));

        Employer applicant = employerRepository.findById(joinRequest.getEmployerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "employerId", joinRequest.getEmployerId()));

        joinRequest.setStatus("REJECTED");
        joinRequest.setReviewedBy(reviewerId);
        joinRequest.setReviewedAt(Instant.now());
        joinRequestRepository.save(joinRequest);

        Notification notification = Notification.builder()
                .userId(joinRequest.getEmployerId())
                .type("JOIN_REQUEST_REJECTED")
                .title("Yêu cầu gia nhập bị từ chối")
                .content(String.format("Yêu cầu gia nhập công ty %s đã bị từ chối.",
                        company.getCompanyName()))
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        return ApiResponse.success("Đã từ chối yêu cầu gia nhập.", buildResponse(joinRequest, company, applicant));
    }

    // Why: shared validation for both approve and reject — check request exists, is PENDING, and reviewer is company owner
    private CompanyJoinRequest findAndValidate(UUID requestId, UUID reviewerId) {
        CompanyJoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("JoinRequest", "id", requestId));

        if (!"PENDING".equals(joinRequest.getStatus())) {
            throw new IllegalStateException("Yêu cầu này đã được xử lý!");
        }

        Company company = companyRepository.findById(joinRequest.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "companyId", joinRequest.getCompanyId()));

        // Why: only the employer who created the company can approve/reject join requests
        if (company.getCreatedBy() == null || !company.getCreatedBy().equals(reviewerId)) {
            throw new ForbiddenException("join request", "review");
        }

        return joinRequest;
    }

    private JoinRequestResponseDto buildResponse(CompanyJoinRequest req, Company company, Employer employer) {
        return new JoinRequestResponseDto(
                req.getId(),
                req.getCompanyId(),
                company.getCompanyName(),
                req.getEmployerId(),
                employer.getName(),
                employer.getEmail(),
                req.getMessage(),
                req.getStatus(),
                req.getCreatedAt()
        );
    }
}
