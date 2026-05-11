package com.inclusivefinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OverdueResponse(
    Long id,
    Long loanId,
    Long enterpriseId,
    String enterpriseName,
    Integer overdueDays,
    BigDecimal overdueAmount,
    BigDecimal penalty,
    LocalDate startDate,
    LocalDate endDate,
    String status
) {}
