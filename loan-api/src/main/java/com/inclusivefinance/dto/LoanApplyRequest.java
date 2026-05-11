package com.inclusivefinance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanApplyRequest(
    @NotNull @DecimalMin("0.01") BigDecimal loanAmount,
    @NotNull @Min(1) Integer loanTerm,
    @NotBlank String loanPurpose,
    @NotBlank String repaymentMethod
) {}
