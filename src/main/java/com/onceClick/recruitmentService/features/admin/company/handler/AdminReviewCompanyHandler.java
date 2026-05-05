package com.onceClick.recruitmentService.features.admin.company.handler;

import com.onceClick.recruitmentService.features.admin.company.dto.AdminCompanyResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Notification;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
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
public class AdminReviewCompanyHandler {

    private final CompanyRepository companyRepository;
    private final NotificationRepository notificationRepository;

    // Admin approves a pending company
    @Transactional
    public ApiResponse<AdminCompanyResponseDto> approve(UUID companyId) {
        log.info("Admin approving company {}", companyId);

        Company company = findAndValidatePending(companyId);

        // Activate the company
        company.setStatus("active");
        company.setVerifiedAt(Instant.now());
        company.setVerificationLevel("lv3");
        Company saved = companyRepository.save(company);

        // Notify the company owner
        sendNotification(saved, "COMPANY_APPROVED",
                "Công ty đã được phê duyệt",
                String.format("Công ty %s đã được admin phê duyệt và kích hoạt thành công!",
                        saved.getCompanyName()));

        return ApiResponse.success("Phê duyệt công ty thành công!", mapToDto(saved));
    }

    // Admin rejects a pending company
    @Transactional
    public ApiResponse<AdminCompanyResponseDto> reject(UUID companyId) {
        log.info("Admin rejecting company {}", companyId);

        Company company = findAndValidatePending(companyId);

        // Mark as rejected
        company.setStatus("rejected");
        Company saved = companyRepository.save(company);

        // Notify the company owner
        sendNotification(saved, "COMPANY_REJECTED",
                "Công ty bị từ chối",
                String.format("Công ty %s đã bị admin từ chối xác minh.",
                        saved.getCompanyName()));

        return ApiResponse.success("Đã từ chối công ty.", mapToDto(saved));
    }

    // Find company by id and ensure it is in pending state
    private Company findAndValidatePending(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company", "companyId", companyId));

        if (!"pending".equals(company.getStatus())) {
            throw new IllegalStateException(
                    "Công ty này không ở trạng thái chờ duyệt!");
        }
        return company;
    }

    // Send notification to company owner if owner exists
    private void sendNotification(Company company, String type,
                                  String title, String content) {
        // Skip when seed data company has no owner (createdBy is null)
        if (company.getCreatedBy() == null) {
            log.warn("Company {} has no owner (createdBy is null), skipping notification",
                    company.getCompanyId());
            return;
        }
        Notification notification = Notification.builder()
                .userId(company.getCreatedBy())
                .type(type)
                .title(title)
                .content(content)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    // Map Company entity to admin DTO
    private AdminCompanyResponseDto mapToDto(Company c) {
        return new AdminCompanyResponseDto(
                c.getCompanyId(), c.getCompanyName(), c.getTaxCode(),
                c.getBusinessLicenseUrl(), c.getBusinessRepName(), c.getFinancialProofUrl(),
                c.getLogoUrl(),  c.getBackgroundUrl(),  c.getWebsiteUrl(), c.getProvinceCode(), c.getIndustry(),
                c.getSizeRange(), c.getOverview(), c.getAddress(), c.getCreatedBy(),
                c.getStatus(), c.getVerificationLevel(), c.getVerifiedAt(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}
