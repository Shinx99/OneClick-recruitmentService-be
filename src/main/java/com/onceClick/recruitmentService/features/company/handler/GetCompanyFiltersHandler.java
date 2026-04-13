package com.onceClick.recruitmentService.features.company.handler;

import com.onceClick.recruitmentService.features.company.dto.response.FilterOptionsResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetCompanyFiltersHandler {

    private final CompanyRepository companyRepository;

    public ApiResponse<FilterOptionsResponseDto> getFilters() {
        FilterOptionsResponseDto filters = FilterOptionsResponseDto.builder()
                .industries(companyRepository.findDistinctIndustries())
                .companySizes(companyRepository.findDistinctSizeRanges())
                .provinces(companyRepository.findDistinctProvinces())
                .build();

        return ApiResponse.success(filters);
    }
}