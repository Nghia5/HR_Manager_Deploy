package com.example.nhom3.position.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PositionRequest(
        @NotBlank(message = "Mã chức vụ không được để trống") String code,

        @NotBlank(message = "Tên chức vụ không được để trống") String name,

        String description,

        String level,

        @NotNull(message = "Khoa/Phòng ban không được để trống") UUID departmentId) {
}