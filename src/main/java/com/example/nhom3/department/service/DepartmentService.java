package com.example.nhom3.department.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.nhom3.department.model.dto.DepartmentRequest;
import com.example.nhom3.department.model.dto.DepartmentResponse;
import com.example.nhom3.department.model.entity.Department;
import com.example.nhom3.department.repository.DepartmentRepository;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    // 1. LẤY DANH SÁCH (Chỉ lấy những khoa đang hoạt động)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 2. THÊM MỚI
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.code())) {
            throw new RuntimeException("Lỗi: Mã khoa đã tồn tại!");
        }

        Department dept = new Department();
        dept.setCode(request.code());
        dept.setName(request.name());
        dept.setDescription(request.description());
        dept.setEstablishedDate(request.establishedDate());

        Department savedDept = departmentRepository.save(dept);
        return mapToResponse(savedDept);
    }

    // 3. CẬP NHẬT
    public DepartmentResponse updateDepartment(UUID id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Khoa/Phòng ban!"));

        // Nếu mã Code thay đổi, phải kiểm tra xem mã mới có bị trùng với khoa khác
        // không
        if (!dept.getCode().equals(request.code()) && departmentRepository.existsByCode(request.code())) {
            throw new RuntimeException("Lỗi: Mã khoa mới đã tồn tại trong hệ thống!");
        }

        dept.setCode(request.code());
        dept.setName(request.name());
        dept.setDescription(request.description());
        dept.setEstablishedDate(request.establishedDate());

        Department updatedDept = departmentRepository.save(dept);
        return mapToResponse(updatedDept);
    }

    // 4. XÓA MỀM (Soft Delete)
    public void deleteDepartment(UUID id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Khoa/Phòng ban!"));

        dept.setIsActive(false); // Vô hiệu hóa
        dept.setDeletedAt(LocalDateTime.now()); // Lưu thời gian xóa

        departmentRepository.save(dept);
    }

    // Hàm Helper chuyển đổi Entity -> DTO
    private DepartmentResponse mapToResponse(Department dept) {
        return new DepartmentResponse(
                dept.getId(),
                dept.getCode(),
                dept.getName(),
                dept.getDescription(),
                dept.getEstablishedDate(),
                dept.getIsActive());
    }
}