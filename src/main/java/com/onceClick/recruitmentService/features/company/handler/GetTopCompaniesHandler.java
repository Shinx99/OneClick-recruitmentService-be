package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.response.TopCompanyResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTopCompaniesHandler {
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<TopCompanyResponseDto>> getTop6Companies() {
        List<Company> companies = companyRepository. findTopCompaniesBySize(PageRequest.of(0, 6)).getContent();

        List<TopCompanyResponseDto> response = companies.stream()
                .map(c -> new TopCompanyResponseDto(
                        c.getCompanyId(), c.getCompanyName(), c.getLogoUrl(),
                        c.getIndustry(), c.getProvinceCode(), c.getSizeRange(),
                        c.getWebsiteUrl(), c.getVerifiedAt()
                )).toList();

        return ApiResponse.success("Lấy top 6 thành công", response);
    }
}