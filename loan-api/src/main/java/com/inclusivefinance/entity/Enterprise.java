package com.inclusivefinance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enterprise")
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50, unique = true)
    private String creditCode;

    @Column(length = 50)
    private String legalPerson;

    @Column(length = 18)
    private String legalIdCard;

    @Column(length = 20)
    private String contactPhone;

    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String industry;

    @Column(precision = 15, scale = 2)
    private BigDecimal registeredCapital;

    private LocalDate establishDate;

    private Integer employeeCount;

    @Column(precision = 15, scale = 2)
    private BigDecimal annualRevenue;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Enterprise() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCreditCode() { return creditCode; }
    public void setCreditCode(String creditCode) { this.creditCode = creditCode; }
    public String getLegalPerson() { return legalPerson; }
    public void setLegalPerson(String legalPerson) { this.legalPerson = legalPerson; }
    public String getLegalIdCard() { return legalIdCard; }
    public void setLegalIdCard(String legalIdCard) { this.legalIdCard = legalIdCard; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public BigDecimal getRegisteredCapital() { return registeredCapital; }
    public void setRegisteredCapital(BigDecimal registeredCapital) { this.registeredCapital = registeredCapital; }
    public LocalDate getEstablishDate() { return establishDate; }
    public void setEstablishDate(LocalDate establishDate) { this.establishDate = establishDate; }
    public Integer getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }
    public BigDecimal getAnnualRevenue() { return annualRevenue; }
    public void setAnnualRevenue(BigDecimal annualRevenue) { this.annualRevenue = annualRevenue; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
