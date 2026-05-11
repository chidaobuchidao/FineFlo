package com.inclusivefinance.controller;

import com.inclusivefinance.common.PageResult;
import com.inclusivefinance.common.Result;
import com.inclusivefinance.dto.*;
import com.inclusivefinance.security.SecurityUtils;
import com.inclusivefinance.service.EnterpriseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/enterprise")
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    public EnterpriseController(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
    }

    @GetMapping("/loans")
    public Result<PageResult<LoanDetailResponse>> getLoans(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Long enterpriseId = SecurityUtils.getEnterpriseId(request);
        return Result.success(enterpriseService.getLoans(enterpriseId, status, page, size));
    }

    @PostMapping("/loans")
    public Result<LoanDetailResponse> applyLoan(HttpServletRequest request,
                                                 @Valid @RequestBody LoanApplyRequest req) {
        Long enterpriseId = SecurityUtils.getEnterpriseId(request);
        Long userId = SecurityUtils.getUserId(request);
        return Result.success(enterpriseService.applyLoan(enterpriseId, userId, req));
    }

    @GetMapping("/loans/{id}")
    public Result<LoanDetailResponse> getLoanDetail(@PathVariable Long id) {
        return Result.success(enterpriseService.getLoanDetail(id));
    }

    @PostMapping("/loans/{id}/sign")
    public Result<Void> signLoan(@PathVariable Long id) {
        enterpriseService.signLoan(id);
        return Result.success(null);
    }

    @GetMapping("/repayments")
    public Result<List<RepaymentResponse>> getRepayments(@RequestParam(required = false) Long loanId,
                                                          @RequestParam(required = false) String status) {
        if (loanId == null) return Result.success(List.of());
        return Result.success(enterpriseService.getRepayments(loanId, status));
    }

    @PostMapping("/repayments/{id}/pay")
    public Result<Void> payRepayment(@PathVariable Long id, HttpServletRequest request) {
        Long enterpriseId = SecurityUtils.getEnterpriseId(request);
        enterpriseService.payRepayment(id, enterpriseId);
        return Result.success(null);
    }

    @GetMapping("/overdue")
    public Result<List<OverdueResponse>> getOverdues(HttpServletRequest request) {
        Long enterpriseId = SecurityUtils.getEnterpriseId(request);
        return Result.success(enterpriseService.getOverdues(enterpriseId));
    }

    @GetMapping("/calculator")
    public Result<CalculatorResponse> calculate(
            @RequestParam BigDecimal amount,
            @RequestParam int term,
            @RequestParam(defaultValue = "0.0435") BigDecimal rate) {
        return Result.success(enterpriseService.calculate(amount, term, rate));
    }
}
