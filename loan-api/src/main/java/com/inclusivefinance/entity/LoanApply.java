package com.inclusivefinance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_apply")
public class LoanApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enterprise_id", nullable = false)
    private Long enterpriseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false)
    private Integer loanTerm;

    @Column(length = 255)
    private String loanPurpose;

    @Column(precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(length = 20)
    private String repaymentMethod;

    @Column(length = 20)
    private String status = "PENDING";

    @Column(updatable = false)
    private LocalDateTime applyDate;

    private LocalDate approveDate;

    private Long approveUserId;

    @Column(length = 500)
    private String approveComment;

    @Column(precision = 5, scale = 2)
    private BigDecimal creditScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id", insertable = false, updatable = false)
    private Enterprise enterprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserInfo user;

    @PrePersist void onCreate() { applyDate = LocalDateTime.now(); }

    public LoanApply() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Long enterpriseId) { this.enterpriseId = enterpriseId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
    public Integer getLoanTerm() { return loanTerm; }
    public void setLoanTerm(Integer loanTerm) { this.loanTerm = loanTerm; }
    public String getLoanPurpose() { return loanPurpose; }
    public void setLoanPurpose(String loanPurpose) { this.loanPurpose = loanPurpose; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public String getRepaymentMethod() { return repaymentMethod; }
    public void setRepaymentMethod(String repaymentMethod) { this.repaymentMethod = repaymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getApplyDate() { return applyDate; }
    public LocalDate getApproveDate() { return approveDate; }
    public void setApproveDate(LocalDate approveDate) { this.approveDate = approveDate; }
    public Long getApproveUserId() { return approveUserId; }
    public void setApproveUserId(Long approveUserId) { this.approveUserId = approveUserId; }
    public String getApproveComment() { return approveComment; }
    public void setApproveComment(String approveComment) { this.approveComment = approveComment; }
    public BigDecimal getCreditScore() { return creditScore; }
    public void setCreditScore(BigDecimal creditScore) { this.creditScore = creditScore; }
    public Enterprise getEnterprise() { return enterprise; }
    public UserInfo getUser() { return user; }
}
