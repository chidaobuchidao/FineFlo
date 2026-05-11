package com.inclusivefinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RepaymentResponse(
    Long id,
    Long loanId,
    Integer periodNo,
    BigDecimal amount,
    BigDecimal paidAmount,
    LocalDate dueDate,
    LocalDate paidDate,
    String status
) {}
