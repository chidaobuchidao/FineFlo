package com.inclusivefinance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RiskQueryResponse(
    Long enterpriseId,
    String enterpriseName,
    BigDecimal currentScore,
    String riskLevel,
    String modelVersion,
    LocalDateTime lastEvaluated,
    List<ScoreHistory> history
) {
    public record ScoreHistory(Long id, BigDecimal score, String modelVersion, LocalDateTime evaluatedAt) {}
}
