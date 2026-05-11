package com.inclusivefinance.service;

import com.inclusivefinance.client.RiskEngineClient;
import com.inclusivefinance.common.BusinessException;
import com.inclusivefinance.common.PageResult;
import com.inclusivefinance.dto.*;
import com.inclusivefinance.entity.*;
import com.inclusivefinance.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EnterpriseService {

    private final LoanApplyRepository loanRepo;
    private final RepaymentRepository repaymentRepo;
    private final OverdueRepository overdueRepo;
    private final CreditScoreRepository creditScoreRepo;
    private final EnterpriseRepository enterpriseRepo;
    private final UserInfoRepository userRepo;
    private final RiskEngineClient riskEngineClient;

    public EnterpriseService(LoanApplyRepository loanRepo, RepaymentRepository repaymentRepo,
                             OverdueRepository overdueRepo, CreditScoreRepository creditScoreRepo,
                             EnterpriseRepository enterpriseRepo, UserInfoRepository userRepo,
                             RiskEngineClient riskEngineClient) {
        this.loanRepo = loanRepo;
        this.repaymentRepo = repaymentRepo;
        this.overdueRepo = overdueRepo;
        this.creditScoreRepo = creditScoreRepo;
        this.enterpriseRepo = enterpriseRepo;
        this.userRepo = userRepo;
        this.riskEngineClient = riskEngineClient;
    }

    // ─── Loan List ────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public PageResult<LoanDetailResponse> getLoans(Long enterpriseId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LoanApply> result;
        if (status != null && !status.isEmpty()) {
            result = loanRepo.findByEnterpriseIdAndStatusOrderByApplyDateDesc(enterpriseId, status, pageable);
        } else {
            result = loanRepo.findByEnterpriseIdOrderByApplyDateDesc(enterpriseId, pageable);
        }

        List<LoanDetailResponse> records = result.getContent().stream()
                .map(this::toLoanDetail)
                .toList();
        return new PageResult<>(result.getTotalElements(), page, size, records);
    }

    // ─── Create Loan Application ──────────────────────────────────
    @Transactional
    public LoanDetailResponse applyLoan(Long enterpriseId, Long userId, LoanApplyRequest request) {
        Enterprise enterprise = enterpriseRepo.findById(enterpriseId)
                .orElseThrow(() -> new BusinessException(404, "Enterprise not found"));

        LoanApply loan = new LoanApply();
        loan.setEnterpriseId(enterpriseId);
        loan.setUserId(userId);
        loan.setLoanAmount(request.loanAmount());
        loan.setLoanTerm(request.loanTerm());
        loan.setLoanPurpose(request.loanPurpose());
        loan.setRepaymentMethod(request.repaymentMethod());
        loan.setInterestRate(getDefaultRate(request.loanTerm()));
        loan.setStatus("PENDING");

        // Call risk engine for credit score
        try {
            Map<String, Object> features = Map.of(
                    "enterpriseId", enterpriseId,
                    "loanAmount", request.loanAmount().doubleValue(),
                    "loanTerm", request.loanTerm(),
                    "industry", enterprise.getIndustry() != null ? enterprise.getIndustry() : ""
            );
            var riskResult = riskEngineClient.predict(enterpriseId, features);
            if (riskResult != null && riskResult.get("creditScore") != null) {
                loan.setCreditScore(BigDecimal.valueOf(((Number) riskResult.get("creditScore")).doubleValue()));
            }
        } catch (Exception e) {
            loan.setCreditScore(BigDecimal.valueOf(70.0));
        }

        loanRepo.save(loan);
        return toLoanDetail(loan);
    }

    // ─── Loan Detail ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public LoanDetailResponse getLoanDetail(Long loanId) {
        LoanApply loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new BusinessException(404, "Loan not found"));
        return toLoanDetail(loan);
    }

    // ─── Sign Contract ────────────────────────────────────────────
    @Transactional
    public void signLoan(Long loanId) {
        LoanApply loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new BusinessException(404, "Loan not found"));

        if (!"APPROVED".equals(loan.getStatus())) {
            throw new BusinessException(400, "Only approved loans can be signed");
        }

        loan.setStatus("GRANTED");
        loanRepo.save(loan);

        // Generate repayment schedule
        generateRepayments(loan);
    }

    private void generateRepayments(LoanApply loan) {
        BigDecimal amount = loan.getLoanAmount();
        int term = loan.getLoanTerm();
        BigDecimal rate = loan.getInterestRate();
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        LocalDate startDate = LocalDate.now();

        if ("EQUAL_INSTALLMENT".equals(loan.getRepaymentMethod())) {
            BigDecimal payment = equalInstallmentPayment(amount, monthlyRate, term);
            BigDecimal remaining = amount;
            for (int i = 1; i <= term; i++) {
                BigDecimal interest = remaining.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal principal = payment.subtract(interest).setScale(2, RoundingMode.HALF_UP);
                if (i == term) {
                    principal = remaining;
                }
                remaining = remaining.subtract(principal);

                Repayment rp = new Repayment();
                rp.setLoanId(loan.getId());
                rp.setPeriodNo(i);
                rp.setAmount(payment.setScale(2, RoundingMode.HALF_UP));
                rp.setDueDate(startDate.plusMonths(i));
                rp.setStatus("UNPAID");
                repaymentRepo.save(rp);
            }
        } else {
            BigDecimal monthlyPrincipal = amount.divide(BigDecimal.valueOf(term), 2, RoundingMode.HALF_UP);
            BigDecimal remaining = amount;
            for (int i = 1; i <= term; i++) {
                BigDecimal interest = remaining.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal principal = i == term ? remaining : monthlyPrincipal;
                remaining = remaining.subtract(principal);

                Repayment rp = new Repayment();
                rp.setLoanId(loan.getId());
                rp.setPeriodNo(i);
                rp.setAmount(principal.add(interest));
                rp.setDueDate(startDate.plusMonths(i));
                rp.setStatus("UNPAID");
                repaymentRepo.save(rp);
            }
        }
    }

    // ─── Repayments ───────────────────────────────────────────────
    public List<RepaymentResponse> getRepayments(Long loanId, String status) {
        List<Repayment> list;
        if (status != null && !status.isEmpty()) {
            list = repaymentRepo.findByLoanIdAndStatus(loanId, status);
        } else {
            list = repaymentRepo.findByLoanIdOrderByPeriodNo(loanId);
        }
        return list.stream().map(r -> new RepaymentResponse(
                r.getId(), r.getLoanId(), r.getPeriodNo(), r.getAmount(),
                r.getPaidAmount(), r.getDueDate(), r.getPaidDate(), r.getStatus()
        )).toList();
    }

    @Transactional
    public void payRepayment(Long repaymentId, Long enterpriseId) {
        Repayment rp = repaymentRepo.findById(repaymentId)
                .orElseThrow(() -> new BusinessException(404, "Repayment record not found"));

        LoanApply loan = loanRepo.findById(rp.getLoanId())
                .orElseThrow(() -> new BusinessException(404, "Loan not found"));

        if (!loan.getEnterpriseId().equals(enterpriseId)) {
            throw new BusinessException(403, "Access denied");
        }
        if (!"UNPAID".equals(rp.getStatus()) && !"OVERDUE".equals(rp.getStatus())) {
            throw new BusinessException(400, "This installment is already paid");
        }

        rp.setPaidAmount(rp.getAmount());
        rp.setPaidDate(LocalDate.now());
        rp.setStatus("PAID");
        repaymentRepo.save(rp);

        // Check if all paid
        List<Repayment> all = repaymentRepo.findByLoanIdOrderByPeriodNo(rp.getLoanId());
        boolean allPaid = all.stream().allMatch(x -> "PAID".equals(x.getStatus()));
        if (allPaid) {
            loan.setStatus("REPAID");
            loanRepo.save(loan);
        }
    }

    // ─── Overdue ──────────────────────────────────────────────────
    public List<OverdueResponse> getOverdues(Long enterpriseId) {
        List<Overdue> list = overdueRepo.findByEnterpriseId(enterpriseId);
        return list.stream().map(o -> {
            Enterprise ent = enterpriseRepo.findById(o.getEnterpriseId()).orElse(null);
            return new OverdueResponse(
                    o.getId(), o.getLoanId(), o.getEnterpriseId(),
                    ent != null ? ent.getName() : "",
                    o.getOverdueDays(), o.getOverdueAmount(), o.getPenalty(),
                    o.getStartDate(), o.getEndDate(), o.getStatus()
            );
        }).toList();
    }

    // ─── Calculator ───────────────────────────────────────────────
    public CalculatorResponse calculate(BigDecimal amount, int term, BigDecimal rate) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal payment = equalInstallmentPayment(amount, monthlyRate, term);
        BigDecimal totalPayment = payment.multiply(BigDecimal.valueOf(term));
        BigDecimal totalInterest = totalPayment.subtract(amount);

        List<CalculatorResponse.ScheduleItem> schedule = new ArrayList<>();
        BigDecimal remaining = amount;
        for (int i = 1; i <= term; i++) {
            BigDecimal interest = remaining.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = payment.subtract(interest).setScale(2, RoundingMode.HALF_UP);
            if (i == term) {
                principal = remaining.setScale(2, RoundingMode.HALF_UP);
            }
            remaining = remaining.subtract(principal);
            schedule.add(new CalculatorResponse.ScheduleItem(i, principal, interest, remaining));
        }

        return new CalculatorResponse("EQUAL_INSTALLMENT", payment.setScale(2, RoundingMode.HALF_UP),
                totalInterest.setScale(2, RoundingMode.HALF_UP),
                totalPayment.setScale(2, RoundingMode.HALF_UP), schedule);
    }

    // ─── Helpers ──────────────────────────────────────────────────
    private BigDecimal equalInstallmentPayment(BigDecimal principal, BigDecimal monthlyRate, int term) {
        double p = principal.doubleValue();
        double r = monthlyRate.doubleValue();
        double payment = p * r * Math.pow(1 + r, term) / (Math.pow(1 + r, term) - 1);
        return BigDecimal.valueOf(payment).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getDefaultRate(int term) {
        if (term <= 6) return BigDecimal.valueOf(0.0400);
        if (term <= 12) return BigDecimal.valueOf(0.0435);
        if (term <= 24) return BigDecimal.valueOf(0.0450);
        return BigDecimal.valueOf(0.0480);
    }

    private LoanDetailResponse toLoanDetail(LoanApply loan) {
        Enterprise ent = loan.getEnterprise();
        UserInfo user = loan.getUser();
        return new LoanDetailResponse(
                loan.getId(), loan.getEnterpriseId(),
                ent != null ? ent.getName() : "",
                loan.getUserId(),
                user != null ? user.getRealName() : "",
                loan.getLoanAmount(), loan.getLoanTerm(),
                loan.getLoanPurpose(), loan.getInterestRate(),
                loan.getRepaymentMethod(), loan.getStatus(),
                loan.getApplyDate(), loan.getApproveDate(),
                loan.getApproveUserId(), loan.getApproveComment(),
                loan.getCreditScore()
        );
    }
}
