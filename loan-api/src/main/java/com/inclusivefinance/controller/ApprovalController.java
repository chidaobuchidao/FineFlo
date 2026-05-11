package com.inclusivefinance.controller;

import com.inclusivefinance.common.PageResult;
import com.inclusivefinance.common.Result;
import com.inclusivefinance.dto.*;
import com.inclusivefinance.entity.Enterprise;
import com.inclusivefinance.security.SecurityUtils;
import com.inclusivefinance.service.ApprovalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/enterprises")
    public Result<PageResult<EnterpriseListResponse>> getEnterprises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(approvalService.getEnterprises(keyword, page, size));
    }

    @GetMapping("/enterprises/{id}")
    public Result<Enterprise> getEnterpriseDetail(@PathVariable Long id) {
        return Result.success(approvalService.getEnterpriseDetail(id));
    }

    @GetMapping("/loans")
    public Result<PageResult<LoanDetailResponse>> getLoans(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.success(approvalService.getApprovalLoans(status, page, size));
    }

    @PutMapping("/loans/{id}/approve")
    public Result<Void> approveLoan(@PathVariable Long id,
                                     @RequestBody ApprovalRequest req,
                                     HttpServletRequest request) {
        Long approverId = SecurityUtils.getUserId(request);
        approvalService.approveLoan(id, req.action(), req.comment(), approverId);
        return Result.success(null);
    }

    @GetMapping("/loans/{id}")
    public Result<LoanDetailResponse> getLoanDetail(@PathVariable Long id) {
        return Result.success(approvalService.getApprovalLoanDetail(id));
    }

    @GetMapping("/disbursements")
    public Result<PageResult<LoanDetailResponse>> getDisbursements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.success(approvalService.getDisbursements(status, page, size));
    }

    @PutMapping("/disbursements/{loanId}/grant")
    public Result<Void> grantLoan(@PathVariable Long loanId, HttpServletRequest request) {
        Long approverId = SecurityUtils.getUserId(request);
        approvalService.grantLoan(loanId, approverId);
        return Result.success(null);
    }

    @GetMapping("/repayments")
    public Result<List<RepaymentResponse>> getRepayments(
            @RequestParam(required = false) Long loanId,
            @RequestParam(required = false) String status) {
        if (loanId == null) return Result.success(List.of());
        return Result.success(approvalService.getApprovalRepayments(loanId, status));
    }

    @GetMapping("/overdues")
    public Result<PageResult<OverdueResponse>> getOverdues(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(approvalService.getOverdues(page, size));
    }

    @GetMapping("/risk-query")
    public Result<RiskQueryResponse> queryRisk(@RequestParam Long enterpriseId) {
        return Result.success(approvalService.queryRisk(enterpriseId));
    }

    @PostMapping("/risk-evaluate")
    public Result<Map<String, Object>> evaluateRisk(@RequestBody RiskEvaluateRequest req) {
        return Result.success(approvalService.evaluateRisk(req.enterpriseId()));
    }
}
