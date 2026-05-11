package com.inclusivefinance.controller;

import com.inclusivefinance.common.Result;
import com.inclusivefinance.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/loan-overview")
    public Result<Map<String, Object>> loanOverview(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(statisticsService.loanOverview(startDate, endDate));
    }

    @GetMapping("/disbursement-trend")
    public Result<List<Map<String, Object>>> disbursementTrend() {
        return Result.success(statisticsService.disbursementTrend());
    }

    @GetMapping("/repayment-trend")
    public Result<List<Map<String, Object>>> repaymentTrend() {
        return Result.success(statisticsService.repaymentTrend());
    }

    @GetMapping("/overdue-analysis")
    public Result<Map<String, Object>> overdueAnalysis() {
        return Result.success(statisticsService.overdueAnalysis());
    }
}
