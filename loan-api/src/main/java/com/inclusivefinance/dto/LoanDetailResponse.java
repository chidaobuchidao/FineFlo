package com.inclusivefinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanDetailResponse(
    Long id,
    Long enterpriseId,
    String enterpriseName,
    Long userId,
    String userName,
    BigDecimal loanAmount,
    Integer loanTerm,
    String loanPurpose,
    BigDecimal interestRate,
    String repaymentMethod,
    String status,
    LocalDateTime applyDate,
    LocalDate approveDate,
    Long approveUserId,
    String approveComment,
    BigDecimal creditScore
) {}
