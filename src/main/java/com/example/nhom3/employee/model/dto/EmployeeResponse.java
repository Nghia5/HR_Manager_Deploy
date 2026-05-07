package com.example.nhom3.employee.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String code,
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String email,
        String phone,
        String address,

        // Trả về cả ID và Tên để FE dễ làm UI
        UUID departmentId,
        String departmentName,

        UUID positionId,
        String positionName,

        UUID userId,
        String username,

        LocalDate hireDate,
        String contractType,
        BigDecimal salaryCoefficient,
        String academicDegree,
        String academicTitle,
        String specialization,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean isActive) {
}