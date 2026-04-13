package com.onceClick.recruitmentService.features.company.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FilterOptionsResponseDto {
    private List<String> industries;
    private List<String> companySizes;
    private List<String> provinces;
}