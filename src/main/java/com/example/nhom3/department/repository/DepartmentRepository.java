package com.example.nhom3.department.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nhom3.department.model.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    // Lấy danh sách các khoa chưa bị xóa mềm (is_active = 1)
    List<Department> findByIsActiveTrue();

    // Kiểm tra xem mã khoa đã tồn tại chưa (Dùng khi Thêm mới)
    boolean existsByCode(String code);
}