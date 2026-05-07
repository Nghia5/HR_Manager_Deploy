package com.example.nhom3.department.model.dto;

import java.time.LocalDate;
import java.util.UUID;

public record DepartmentResponse(
        UUID id,
        String code,
        String name,
        String description,
        LocalDate establishedDate,
        Boolean isActive) {
}