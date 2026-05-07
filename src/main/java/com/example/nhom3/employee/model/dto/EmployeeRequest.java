package com.example.nhom3.employee.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmployeeRequest(
        @NotBlank(message = "Mã nhân viên không được để trống") String code,

        @NotBlank(message = "Họ tên không được để trống") String fullName,

        LocalDate dateOfBirth,
        String gender,

        @Email(message = "Email không đúng định dạng") String email,

        String phone,
        String address,

        // Các ID khóa ngoại
        UUID departmentId,
        UUID positionId,
        UUID userId, // Có thể null nếu chưa cấp tài khoản đăng nhập

        // Thông tin công việc
        LocalDate hireDate,
        String contractType,
        BigDecimal salaryCoefficient,
        String academicDegree,
        String academicTitle,
        String specialization,
        LocalDateTime startDate,
        LocalDateTime endDate) {
}