package com.onceClick.recruitmentService.features.admin.dashboard.controller;

import com.onceClick.recruitmentService.features.admin.dashboard.dto.DashboardCountsDTO;
import com.onceClick.recruitmentService.features.admin.dashboard.dto.GrowthDataDTO;
import com.onceClick.recruitmentService.features.admin.dashboard.dto.RecentActivityGroupDTO;
import com.onceClick.recruitmentService.features.admin.dashboard.handler.DashboardHandler;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final DashboardHandler dashboardHandler;

    @GetMapping("/counts")
    public ResponseEntity<ApiResponse<DashboardCountsDTO>> getCounts(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ){
        DashboardCountsDTO counts;
        if(month != null){
            counts = dashboardHandler.getAdminCountsByMonth(month);
        } else {
            counts = dashboardHandler.getAdminCounts();
        }
        return ResponseEntity.ok(ApiResponse.success(counts));
    }

    @GetMapping("/growth")
    public ResponseEntity<ApiResponse<GrowthDataDTO>> getGrowthData(
            @RequestParam(required = false) Integer months
    ) {
        GrowthDataDTO data = dashboardHandler.getGrowthData(months);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/activities/grouped")
    public ResponseEntity<ApiResponse<List<RecentActivityGroupDTO>>> getRecentActivitiesGrouped() {
        return ResponseEntity.ok(ApiResponse.success(dashboardHandler.getRecentActivitiesGrouped()));
    }
}
