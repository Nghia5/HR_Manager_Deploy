package com.example.nhom3.position.model.dto;

import java.util.UUID;

public record PositionResponse(
        UUID id,
        String code,
        String name,
        String description,
        String level,
        UUID departmentId,
        String departmentName, // Hiển thị tên Khoa trên UI
        Boolean isActive) {
}