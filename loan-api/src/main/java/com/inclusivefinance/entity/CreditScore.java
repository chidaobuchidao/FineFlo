package com.inclusivefinance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_score")
public class CreditScore {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enterprise_id", nullable = false)
    private Long enterpriseId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(length = 20)
    private String modelVersion;

    @Column(columnDefinition = "TEXT")
    private String features;

    @Column(updatable = false)
    private LocalDateTime evaluatedAt;

    @PrePersist void onCreate() { evaluatedAt = LocalDateTime.now(); }

    public CreditScore() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Long enterpriseId) { this.enterpriseId = enterpriseId; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
}
