package com.inclusivefinance.dto;

public record EnterpriseListResponse(
    Long id,
    String name,
    String creditCode,
    String legalPerson,
    String contactPhone,
    String industry,
    Integer status
) {}
