package com.example.nhom3.department.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank(message = "Mã khoa không được để trống") String code,

        @NotBlank(message = "Tên khoa không được để trống") String name,

        String description,

        LocalDate establishedDate) {
}