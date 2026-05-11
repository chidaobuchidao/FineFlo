package com.inclusivefinance.dto;

public record LoginResponse(
    String token,
    long expiresIn,
    UserInfo user
) {
    public record UserInfo(Long id, String username, String realName, String role, Long enterpriseId) {}
}
