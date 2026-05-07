package com.example.nhom3.employee.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nhom3.employee.model.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    // Lấy tất cả nhân viên chưa nghỉ việc / chưa xóa
    List<Employee> findByIsActiveTrue();

    // Kiểm tra trùng mã NV và Email
    boolean existsByCode(String code);

    boolean existsByEmail(String email);

    // Tìm nhân viên theo phòng ban (dùng để lọc trên UI)
    List<Employee> findByDepartmentIdAndIsActiveTrue(UUID departmentId);
}