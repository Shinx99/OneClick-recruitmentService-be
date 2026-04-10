package com.onceClick.recruitmentService.features.admin.dashboard.handler;

import com.onceClick.recruitmentService.features.admin.dashboard.dto.*;
import com.onceClick.recruitmentService.shared.persistence.entity.Candidate;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Employer;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.CandidateRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.EmployerRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardHandler {

    private final CandidateRepository candidateRepository;
    private final CompanyRepository companyRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    public DashboardCountsDTO getAdminCounts() {
        log.info("Bắt đầu lấy số liệu thống kê cho Dashboard Admin");

        long totalCandidates = candidateRepository.countByStatus("active");
        long totalCompanies = companyRepository.countByStatus("active");
        long totalEmployers = employerRepository.countByStatus("active");
        long totalJobs = jobRepository.countByStatus("active");

        log.info("Kết quả - Ứng viên: {}, Công ty: {}, Nhà tuyển dụng: {}, Tin tuyển dụng: {}",
                totalCandidates, totalCompanies, totalEmployers, totalJobs);

        return new DashboardCountsDTO(totalCandidates, totalCompanies, totalEmployers, totalJobs);
    }

    @Transactional(readOnly = true)
    public DashboardCountsDTO getAdminCountsByMonth(YearMonth yearMonth) {
        // Chuyển YearMonth thành Instant (hoặc LocalDateTime)
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        Instant start = startOfMonth.toInstant(ZoneOffset.UTC);
        Instant end = endOfMonth.toInstant(ZoneOffset.UTC);

        long totalCandidates = candidateRepository.countByStatusAndCreatedAtBetween("active", start, end);
        long totalCompanies = companyRepository.countByStatusAndCreatedAtBetween("active", start, end);
        long totalEmployers = employerRepository.countByStatusAndCreatedAtBetween("active", start, end);
        long totalJobs = jobRepository.countByStatusAndCreatedAtBetween("active", start, end);

        return new DashboardCountsDTO(totalCandidates, totalCompanies, totalEmployers, totalJobs);
    }

    @Transactional
    public GrowthDataDTO getGrowthData(Integer months) {
        // Mặc định lấy 6 tháng gần nhất nếu không truyền tham số
        int numMonths = (months != null && months > 0) ? months : 6;

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(numMonths - 1).withDayOfMonth(1);

        Instant start = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = endDate.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        // Lấy dữ liệu thô từ repository
        List<Object[]> candidateStats = candidateRepository.countCandidatesByMonth(start, end);
        List<Object[]> companyStats = companyRepository.countCompaniesByMonth(start, end);
        List<Object[]> employerStats = employerRepository.countEmployersByMonth(start, end);
        List<Object[]> jobStats = jobRepository.countJobsByMonth(start, end);

        // Map thành Map<String, Long> để dễ combine
        Map<String, Long> candidateMap = mapToMonthCount(candidateStats);
        Map<String, Long> companyMap = mapToMonthCount(companyStats);
        Map<String, Long> employerMap = mapToMonthCount(employerStats);
        Map<String, Long> jobMap = mapToMonthCount(jobStats);

        log.info("🗺️ Employer Map: {}", employerMap);

        // Tạo danh sách đầy đủ các tháng trong khoảng
        List<String> allMonths = new ArrayList<>();
        YearMonth current = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate.minusDays(1));
        while (!current.isAfter(endMonth)) {
            allMonths.add(current.toString());
            current = current.plusMonths(1);
        }

        // Xây dựng response
        List<GrowthDataItemDTO> items = new ArrayList<>();
        for (String month : allMonths) {
            items.add(new GrowthDataItemDTO(
                    month,
                    candidateMap.getOrDefault(month, 0L),
                    companyMap.getOrDefault(month, 0L),
                    employerMap.getOrDefault(month, 0L),
                    jobMap.getOrDefault(month, 0L)
            ));
        }

        return new GrowthDataDTO(items);
    }

    private Map<String, Long> mapToMonthCount(List<Object[]> stats) {
        return stats.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    public List<RecentActivityGroupDTO> getRecentActivitiesGrouped() {
        Pageable topThree = PageRequest.of(0, 3);

        // Lấy danh sách mới nhất từ mỗi loại
        List<Candidate> candidates = candidateRepository.findRecentCandidates(topThree);
        List<Company> companies = companyRepository.findRecentCompanies(topThree);
        List<Employer> employers = employerRepository.findRecentEmployers(topThree);
        List<Job> jobs = jobRepository.findRecentJobs(topThree);

        List<RecentActivityGroupDTO> groups = new ArrayList<>();

        // Nhóm Ứng viên
        if (!candidates.isEmpty()) {
            List<ActivityItemDTO> items = candidates.stream()
                    .map(c -> new ActivityItemDTO(
                            c.getCandidateId().toString(),
                            c.getSurname() + " " + c.getName(),
                            c.getCreatedAt()
                    ))
                    .collect(Collectors.toList());
            groups.add(new RecentActivityGroupDTO("candidate", "Ứng viên mới đăng ký", items));
        }

        // Nhóm Công ty
        if (!companies.isEmpty()) {
            List<ActivityItemDTO> items = companies.stream()
                    .map(c -> new ActivityItemDTO(
                            c.getCompanyId().toString(),
                            c.getCompanyName(),
                            c.getCreatedAt()
                    ))
                    .collect(Collectors.toList());
            groups.add(new RecentActivityGroupDTO("company", "Công ty mới đăng ký", items));
        }

        // Nhóm Nhà tuyển dụng
        if (!employers.isEmpty()) {
            List<ActivityItemDTO> items = employers.stream()
                    .map(e -> new ActivityItemDTO(
                            e.getEmployerId().toString(),
                            e.getSurname() + " " + e.getName(),
                            e.getCreatedAt()
                    ))
                    .collect(Collectors.toList());
            groups.add(new RecentActivityGroupDTO("employer", "Nhà tuyển dụng mới", items));
        }

        // Nhóm Tin tuyển dụng
        if (!jobs.isEmpty()) {
            List<ActivityItemDTO> items = jobs.stream()
                    .map(j -> new ActivityItemDTO(
                            j.getJobId().toString(),
                            j.getTitle(),
                            j.getCreatedAt()
                    ))
                    .collect(Collectors.toList());
            groups.add(new RecentActivityGroupDTO("job", "Tin tuyển dụng mới đăng", items));
        }

        return groups;
    }
}
