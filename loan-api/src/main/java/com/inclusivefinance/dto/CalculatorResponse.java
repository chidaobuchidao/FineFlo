package com.inclusivefinance.dto;

import java.math.BigDecimal;
import java.util.List;

public record CalculatorResponse(
    String mode,
    BigDecimal monthlyPayment,
    BigDecimal totalInterest,
    BigDecimal totalPayment,
    List<ScheduleItem> schedule
) {
    public record ScheduleItem(Integer period, BigDecimal principal, BigDecimal interest, BigDecimal remaining) {}
}
