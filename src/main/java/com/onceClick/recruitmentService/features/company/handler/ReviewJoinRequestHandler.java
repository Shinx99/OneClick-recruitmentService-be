package com.onceClick.recruitmentService.features.company.handler;

import com.cloudinary.Api;
import com.onceClick.recruitmentService.features.company.dto.response.JoinRequestResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.BusinessException;
import com.onceClick.recruitmentService.shared.exception.ForbiddenException;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.CompanyJoinRequest;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyJoinRequestRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
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

    // Dùng constant
    private static final String STATUS_PENDING  = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final CompanyJoinRequestRepository joinRequestRepository;
    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;

    private record ValidatedRequest(CompanyJoinRequest joinRequest, Company company) {}


    //-------------------------------------------------------------------------------------------------------------------------------------------
    // APPROVE REQUEST TO JOIN COMPANY
    //-------------------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public ApiResponse<JoinRequestResponseDto> approve(UUID requestId, UUID reviewerId) {
        log.info("Reviewer {} approving join request {}", reviewerId, requestId);

        ValidatedRequest validated = findAndValidate(requestId, reviewerId);
        CompanyJoinRequest joinRequest = validated.joinRequest();
        Company company = validated.company();

        Employer applicant = employerRepository.findById(joinRequest.getEmployerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "employerId", joinRequest.getEmployerId()));

        if (applicant.getCompany() != null) {
            throw new BusinessException("Employer này đã thuộc một công ty khác");
        }

        joinRequest.setStatus(STATUS_APPROVED);
        joinRequest.setReviewedBy(reviewerId);
        joinRequest.setReviewedAt(Instant.now());
        joinRequestRepository.save(joinRequest);

        applicant.setCompany(company);
        applicant.setLevel("level2");
        applicant.setVerificationLevel("lv3");
        employerRepository.save(applicant);

        return ApiResponse.success("Đã duyệt yêu cầu gia nhập!", buildResponse(joinRequest, company, applicant));
    }



    //-------------------------------------------------------------------------------------------------------------------------------------------
    // REJECT REQUEST TO JOIN COMPANY
    //-------------------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public ApiResponse<JoinRequestResponseDto> reject(UUID requestId, UUID reviewerId) {
        log.info("Reviewer {} rejecting join request {}", reviewerId, requestId);

        ValidatedRequest validated = findAndValidate(requestId, reviewerId);
        CompanyJoinRequest joinRequest = validated.joinRequest();
        Company company = validated.company();

        Employer applicant = employerRepository.findById(joinRequest.getEmployerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "employerId", joinRequest.getEmployerId()));

        joinRequest.setStatus(STATUS_REJECTED);
        joinRequest.setReviewedBy(reviewerId);
        joinRequest.setReviewedAt(Instant.now());
        joinRequestRepository.save(joinRequest);

        return ApiResponse.success("Đã từ chối yêu cầu gia nhập.", buildResponse(joinRequest, company, applicant));
    }



    //-------------------------------------------------------------------------------------------------------------------------------------------
    // HELPER
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private ValidatedRequest findAndValidate(UUID requestId, UUID reviewerId) {
        CompanyJoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("JoinRequest", "id", requestId));

        if (!STATUS_PENDING.equals(joinRequest.getStatus())) {
            throw new IllegalStateException("Yêu cầu này đã được xử lý!");
        }

        Company company = companyRepository.findById(joinRequest.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "companyId", joinRequest.getCompanyId()));

        if (company.getCreatedBy() == null || !company.getCreatedBy().equals(reviewerId)) {
            throw new ForbiddenException("join request", "review");
        }

        return new ValidatedRequest(joinRequest, company);
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    // BUILD RESPONSE
    //-------------------------------------------------------------------------------------------------------------------------------------------
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