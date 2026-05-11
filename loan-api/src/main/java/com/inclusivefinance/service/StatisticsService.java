package com.inclusivefinance.service;

import com.inclusivefinance.entity.LoanApply;
import com.inclusivefinance.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final LoanApplyRepository loanRepo;
    private final RepaymentRepository repaymentRepo;
    private final OverdueRepository overdueRepo;

    public StatisticsService(LoanApplyRepository loanRepo, RepaymentRepository repaymentRepo,
                             OverdueRepository overdueRepo) {
        this.loanRepo = loanRepo;
        this.repaymentRepo = repaymentRepo;
        this.overdueRepo = overdueRepo;
    }

    public Map<String, Object> loanOverview(String startDate, String endDate) {
        List<LoanApply> allLoans = loanRepo.findAll();

        long totalApply = allLoans.size();
        long totalApproved = allLoans.stream()
                .filter(l -> "APPROVED".equals(l.getStatus()) || "GRANTED".equals(l.getStatus())
                        || "REPAID".equals(l.getStatus()) || "OVERDUE".equals(l.getStatus()))
                .count();
        long totalDisbursed = allLoans.stream()
                .filter(l -> "GRANTED".equals(l.getStatus()) || "REPAID".equals(l.getStatus())
                        || "OVERDUE".equals(l.getStatus()))
                .count();
        long totalOverdue = allLoans.stream()
                .filter(l -> "OVERDUE".equals(l.getStatus())).count();
        BigDecimal approvalRate = totalApply > 0
                ? BigDecimal.valueOf(totalApproved).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalApply), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Monthly stats from native query
        List<Object[]> monthlyStats = loanRepo.findMonthlyStats();
        List<Map<String, Object>> monthly = monthlyStats.stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("month", row[0]);
            m.put("apply", row[1]);
            m.put("approved", row[2]);
            m.put("disbursed", row[3]);
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalApply", totalApply);
        result.put("totalApproved", totalApproved);
        result.put("totalDisbursed", totalDisbursed);
        result.put("totalOverdue", totalOverdue);
        result.put("approvalRate", approvalRate.doubleValue());
        result.put("monthly", monthly);
        return result;
    }

    public List<Map<String, Object>> disbursementTrend() {
        List<Object[]> stats = loanRepo.findMonthlyStats();
        return stats.stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("month", row[0]);
            m.put("count", row[3]);
            return m;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> repaymentTrend() {
        List<LoanApply> loans = loanRepo.findAll();
        return loans.stream()
                .filter(l -> "GRANTED".equals(l.getStatus()) || "REPAID".equals(l.getStatus())
                        || "OVERDUE".equals(l.getStatus()))
                .map(l -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("loanId", l.getId());
                    m.put("amount", l.getLoanAmount());
                    m.put("status", l.getStatus());
                    return m;
                }).collect(Collectors.toList());
    }

    public Map<String, Object> overdueAnalysis() {
        List<LoanApply> overdueLoans = loanRepo.findAll().stream()
                .filter(l -> "OVERDUE".equals(l.getStatus()))
                .toList();

        long total = overdueLoans.size();
        BigDecimal totalAmount = overdueLoans.stream()
                .map(LoanApply::getLoanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalOverdue", total);
        result.put("totalAmount", totalAmount);
        result.put("records", overdueLoans.stream().map(l -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("loanId", l.getId());
            m.put("enterpriseId", l.getEnterpriseId());
            m.put("amount", l.getLoanAmount());
            return m;
        }).collect(Collectors.toList()));
        return result;
    }
}
