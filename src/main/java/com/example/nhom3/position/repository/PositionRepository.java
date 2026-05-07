package com.example.nhom3.position.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nhom3.position.model.entity.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    // Lấy danh sách chức vụ chưa bị xóa
    List<Position> findByIsActiveTrue();

    // Kiểm tra trùng mã
    boolean existsByCode(String code);

    // Dùng cho tính năng chọn Khoa -> Lọc ra các Chức vụ tương ứng (sau này làm UI
    // sẽ rất cần)
    List<Position> findByDepartmentIdAndIsActiveTrue(UUID departmentId);
}