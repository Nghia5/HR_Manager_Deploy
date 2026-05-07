package com.example.nhom3.position.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.nhom3.position.model.dto.PositionRequest;
import com.example.nhom3.position.model.dto.PositionResponse;
import com.example.nhom3.department.model.entity.Department;
import com.example.nhom3.position.model.entity.Position;
import com.example.nhom3.department.repository.DepartmentRepository;
import com.example.nhom3.position.repository.PositionRepository;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // 1. LẤY DANH SÁCH
    public List<PositionResponse> getAllPositions() {
        return positionRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 2. THÊM MỚI
    public PositionResponse createPosition(PositionRequest request) {
        if (positionRepository.existsByCode(request.code())) {
            throw new RuntimeException("Lỗi: Mã chức vụ đã tồn tại!");
        }

        // Kiểm tra xem Department có tồn tại không
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Khoa/Phòng ban tham chiếu!"));

        Position pos = new Position();
        pos.setCode(request.code());
        pos.setName(request.name());
        pos.setDescription(request.description());
        pos.setLevel(request.level());
        pos.setDepartment(department); // Gán khóa ngoại

        Position savedPos = positionRepository.save(pos);
        return mapToResponse(savedPos);
    }

    // 3. CẬP NHẬT
    public PositionResponse updatePosition(UUID id, PositionRequest request) {
        Position pos = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Chức vụ!"));

        if (!pos.getCode().equals(request.code()) && positionRepository.existsByCode(request.code())) {
            throw new RuntimeException("Lỗi: Mã chức vụ mới đã tồn tại!");
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Khoa/Phòng ban tham chiếu!"));

        pos.setCode(request.code());
        pos.setName(request.name());
        pos.setDescription(request.description());
        pos.setLevel(request.level());
        pos.setDepartment(department); // Cập nhật lại khóa ngoại nếu có thay đổi

        Position updatedPos = positionRepository.save(pos);
        return mapToResponse(updatedPos);
    }

    // 4. XÓA MỀM
    public void deletePosition(UUID id) {
        Position pos = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Chức vụ!"));

        pos.setIsActive(false);
        pos.setDeletedAt(LocalDateTime.now());

        positionRepository.save(pos);
    }

    // LẤY CHỨC VỤ THEO KHOA (Hỗ trợ Dropdown phụ thuộc trên Frontend)
    public List<PositionResponse> getPositionsByDepartment(UUID departmentId) {
        return positionRepository.findByDepartmentIdAndIsActiveTrue(departmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper map Entity -> DTO
    private PositionResponse mapToResponse(Position pos) {
        return new PositionResponse(
                pos.getId(),
                pos.getCode(),
                pos.getName(),
                pos.getDescription(),
                pos.getLevel(),
                pos.getDepartment() != null ? pos.getDepartment().getId() : null,
                pos.getDepartment() != null ? pos.getDepartment().getName() : "Không xác định",
                pos.getIsActive());
    }
}