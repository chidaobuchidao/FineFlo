package com.inclusivefinance.service;

import com.inclusivefinance.client.RiskEngineClient;
import com.inclusivefinance.common.BusinessException;
import com.inclusivefinance.common.PageResult;
import com.inclusivefinance.dto.*;
import com.inclusivefinance.entity.*;
import com.inclusivefinance.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalService {

    private final EnterpriseRepository enterpriseRepo;
    private final LoanApplyRepository loanRepo;
    private final RepaymentRepository repaymentRepo;
    private final OverdueRepository overdueRepo;
    private final CreditScoreRepository creditScoreRepo;
    private final RiskEngineClient riskEngineClient;

    public ApprovalService(EnterpriseRepository enterpriseRepo, LoanApplyRepository loanRepo,
                           RepaymentRepository repaymentRepo, OverdueRepository overdueRepo,
                           CreditScoreRepository creditScoreRepo, RiskEngineClient riskEngineClient) {
        this.enterpriseRepo = enterpriseRepo;
        this.loanRepo = loanRepo;
        this.repaymentRepo = repaymentRepo;
        this.overdueRepo = overdueRepo;
        this.creditScoreRepo = creditScoreRepo;
        this.riskEngineClient = riskEngineClient;
    }

    // ─── Enterprise Management ────────────────────────────────────
    public PageResult<EnterpriseListResponse> getEnterprises(String keyword, int page, int size) {
        Page<Enterprise> result;
        if (keyword != null && !keyword.isEmpty()) {
            result = enterpriseRepo.findAll(PageRequest.of(page - 1, size));
            // Filter in-memory for simplicity; use Specification for production
            List<Enterprise> filtered = enterpriseRepo.findByNameContaining(keyword);
            int start = (page - 1) * size;
            int end = Math.min(start + size, filtered.size());
            List<EnterpriseListResponse> records = filtered.subList(
                    Math.min(start, filtered.size()), end).stream()
                    .map(this::toEnterpriseSummary)
                    .toList();
            return new PageResult<>((long) filtered.size(), page, size, records);
        }
        result = enterpriseRepo.findAll(PageRequest.of(page - 1, size));
        List<EnterpriseListResponse> records = result.getContent().stream()
                .map(this::toEnterpriseSummary)
                .toList();
        return new PageResult<>(result.getTotalElements(), page, size, records);
    }

    public Enterprise getEnterpriseDetail(Long id) {
        return enterpriseRepo.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Enterprise not found"));
    }

    // ─── Loan Approval ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public PageResult<LoanDetailResponse> getApprovalLoans(String status, int page, int size) {
        Page<LoanApply> result;
        if (status != null && !status.isEmpty()) {
            result = loanRepo.findByStatusOrderByApplyDateDesc(status, PageRequest.of(page - 1, size));
        } else {
            result = loanRepo.findAllByOrderByApplyDateDesc(PageRequest.of(page - 1, size));
        }

        List<LoanDetailResponse> records = result.getContent().stream()
                .map(loan -> {
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
                })
                .toList();
        return new PageResult<>(result.getTotalElements(), page, size, records);
    }

    @Transactional
    public void approveLoan(Long loanId, String action, String comment, Long approverId) {
        LoanApply loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new BusinessException(404, "Loan not found"));

        if (!"PENDING".equals(loan.getStatus())) {
            throw new BusinessException(400, "Only pending loans can be approved/rejected");
        }

        if ("APPROVE".equals(action)) {
            loan.setStatus("APPROVED");
        } else if ("REJECT".equals(action)) {
            loan.setStatus("REJECTED");
        } else {
            throw new BusinessException(400, "Action must be APPROVE or REJECT");
        }

        loan.setApproveUserId(approverId);
        loan.setApproveComment(comment);
        loan.setApproveDate(LocalDate.now());
        loanRepo.save(loan);
    }

    @Transactional(readOnly = true)
    public LoanDetailResponse getApprovalLoanDetail(Long loanId) {
        LoanApply loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new BusinessException(404, "Loan not found"));
        Enterprise ent = loan.getEnterprise();
        UserInfo user = loan.getUser();
        return new LoanDetailResponse(
                loan.getId(), loan.getEnterpriseId(),
                ent != null ? ent.getName() : "",
                loan.getUserId(), user != null ? user.getRealName() : "",
                loan.getLoanAmount(), loan.getLoanTerm(),
                loan.getLoanPurpose(), loan.getInterestRate(),
                loan.getRepaymentMethod(), loan.getStatus(),
                loan.getApplyDate(), loan.getApproveDate(),
                loan.getApproveUserId(), loan.getApproveComment(),
                loan.getCreditScore()
        );
    }

    // ─── Disbursement ─────────────────────────────────────────────
    public PageResult<LoanDetailResponse> getDisbursements(String status, int page, int size) {
        String filterStatus = status != null ? status : "APPROVED";
        return getApprovalLoans(filterStatus, page, size);
    }

    @Transactional
    public void grantLoan(Long loanId, Long approverId) {
        LoanApply loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new BusinessException(404, "Loan not found"));

        if (!"APPROVED".equals(loan.getStatus())) {
            throw new BusinessException(400, "Only approved loans can be granted");
        }

        loan.setStatus("GRANTED");

        // Generate repayments
        Repayment rp = new Repayment();
        rp.setLoanId(loan.getId());
        rp.setPeriodNo(1);
        rp.setAmount(loan.getLoanAmount());
        rp.setDueDate(java.time.LocalDate.now().plusMonths(1));
        rp.setStatus("UNPAID");
        repaymentRepo.save(rp);

        loanRepo.save(loan);
    }

    // ─── Repayment Management ─────────────────────────────────────
    public List<RepaymentResponse> getApprovalRepayments(Long loanId, String status) {
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

    // ─── Overdue Management ───────────────────────────────────────
    public PageResult<OverdueResponse> getOverdues(int page, int size) {
        Page<Overdue> result = overdueRepo.findAllByOrderByStartDateDesc(PageRequest.of(page - 1, size));
        List<OverdueResponse> records = result.getContent().stream().map(o -> {
            Enterprise ent = enterpriseRepo.findById(o.getEnterpriseId()).orElse(null);
            return new OverdueResponse(
                    o.getId(), o.getLoanId(), o.getEnterpriseId(),
                    ent != null ? ent.getName() : "",
                    o.getOverdueDays(), o.getOverdueAmount(), o.getPenalty(),
                    o.getStartDate(), o.getEndDate(), o.getStatus()
            );
        }).toList();
        return new PageResult<>(result.getTotalElements(), page, size, records);
    }

    // ─── Risk ─────────────────────────────────────────────────────
    public RiskQueryResponse queryRisk(Long enterpriseId) {
        Enterprise ent = enterpriseRepo.findById(enterpriseId)
                .orElseThrow(() -> new BusinessException(404, "Enterprise not found"));

        CreditScore latest = creditScoreRepo.findTopByEnterpriseIdOrderByEvaluatedAtDesc(enterpriseId)
                .orElse(null);

        List<CreditScore> history = creditScoreRepo.findByEnterpriseIdOrderByEvaluatedAtDesc(enterpriseId);

        String riskLevel = "MEDIUM";
        if (latest != null) {
            double score = latest.getScore().doubleValue();
            if (score >= 80) riskLevel = "LOW";
            else if (score < 60) riskLevel = "HIGH";
        }

        return new RiskQueryResponse(
                enterpriseId, ent.getName(),
                latest != null ? latest.getScore() : null,
                riskLevel,
                latest != null ? latest.getModelVersion() : null,
                latest != null ? latest.getEvaluatedAt() : null,
                history.stream().map(h -> new RiskQueryResponse.ScoreHistory(
                        h.getId(), h.getScore(), h.getModelVersion(), h.getEvaluatedAt()
                )).toList()
        );
    }

    public Map<String, Object> evaluateRisk(Long enterpriseId) {
        Enterprise ent = enterpriseRepo.findById(enterpriseId)
                .orElseThrow(() -> new BusinessException(404, "Enterprise not found"));

        Map<String, Object> features = Map.of(
                "registeredCapital", ent.getRegisteredCapital() != null ? ent.getRegisteredCapital().doubleValue() : 0,
                "employeeCount", ent.getEmployeeCount() != null ? ent.getEmployeeCount() : 0,
                "annualRevenue", ent.getAnnualRevenue() != null ? ent.getAnnualRevenue().doubleValue() : 0,
                "industry", ent.getIndustry() != null ? ent.getIndustry() : ""
        );

        Map<String, Object> result = riskEngineClient.predict(enterpriseId, features);

        if (result != null && result.get("creditScore") != null) {
            CreditScore cs = new CreditScore();
            cs.setEnterpriseId(enterpriseId);
            cs.setScore(java.math.BigDecimal.valueOf(((Number) result.get("creditScore")).doubleValue()));
            cs.setModelVersion(result.get("modelVersion") != null ? result.get("modelVersion").toString() : "v1.0");
            creditScoreRepo.save(cs);
        }

        return result;
    }

    private EnterpriseListResponse toEnterpriseSummary(Enterprise e) {
        return new EnterpriseListResponse(
                e.getId(), e.getName(), e.getCreditCode(),
                e.getLegalPerson(), e.getContactPhone(),
                e.getIndustry(), e.getStatus()
        );
    }
}
