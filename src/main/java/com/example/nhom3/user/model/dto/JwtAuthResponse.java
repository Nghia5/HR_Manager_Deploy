package com.example.nhom3.user.model.dto;

public record JwtAuthResponse(
        String accessToken,
        String username,
        String tokenType) {
    // Constructor tùy chỉnh: Chỉ cần truyền token và username,
    // hệ thống sẽ tự động gán tokenType là "Bearer"
    public JwtAuthResponse(String accessToken, String username) {
        this(accessToken, username, "Bearer");
    }
}