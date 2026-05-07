package com.example.nhom3.employeeposition.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nhom3.employeeposition.model.entity.EmployeePosition;

@Repository
public interface EmployeePositionRepository extends JpaRepository<EmployeePosition, UUID> {

    // Lấy toàn bộ lịch sử phân công chưa bị xóa
    List<EmployeePosition> findByIsActiveTrue();

    // Lấy lịch sử công tác / kiêm nhiệm của 1 nhân viên cụ thể
    List<EmployeePosition> findByEmployeeIdAndIsActiveTrueOrderByStartDateDesc(UUID employeeId);
}