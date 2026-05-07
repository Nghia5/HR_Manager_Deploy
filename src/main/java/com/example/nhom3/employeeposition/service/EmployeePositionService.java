package com.example.nhom3.employeeposition.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.nhom3.employeeposition.model.dto.EmployeePositionRequest;
import com.example.nhom3.employeeposition.model.dto.EmployeePositionResponse;
import com.example.nhom3.employee.model.entity.Employee;
import com.example.nhom3.employeeposition.model.entity.EmployeePosition;
import com.example.nhom3.position.model.entity.Position;
import com.example.nhom3.employeeposition.repository.EmployeePositionRepository;
import com.example.nhom3.employee.repository.EmployeeRepository;
import com.example.nhom3.position.repository.PositionRepository;

@Service
public class EmployeePositionService {

    @Autowired
    private EmployeePositionRepository employeePositionRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PositionRepository positionRepository;

    public List<EmployeePositionResponse> getAllAssignments() {
        // Lấy tất cả các bản ghi phân công đang active (is_active = 1)
        List<EmployeePosition> list = employeePositionRepository.findByIsActiveTrue();

        // Chuyển đổi từ Entity sang DTO để trả về cho Frontend
        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 1. Lấy lịch sử công tác của 1 nhân viên
    public List<EmployeePositionResponse> getHistoryByEmployee(UUID employeeId) {
        return employeePositionRepository.findByEmployeeIdAndIsActiveTrueOrderByStartDateDesc(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 2. Thêm mới một phân công / kiêm nhiệm
    public EmployeePositionResponse createAssignment(EmployeePositionRequest request) {
        Employee emp = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy nhân viên!"));

        Position pos = positionRepository.findById(request.positionId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy chức vụ!"));

        EmployeePosition ep = new EmployeePosition();
        ep.setEmployee(emp);
        ep.setPosition(pos);
        ep.setDescription(request.description());
        ep.setNote(request.note());
        ep.setStartDate(request.startDate());
        ep.setEndDate(request.endDate());

        EmployeePosition savedEp = employeePositionRepository.save(ep);
        return mapToResponse(savedEp);
    }

    // 3. Cập nhật (Ví dụ: Cập nhật ngày kết thúc khi thôi kiêm nhiệm)
    public EmployeePositionResponse updateAssignment(UUID id, EmployeePositionRequest request) {
        EmployeePosition ep = employeePositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy bản ghi phân công!"));

        Position pos = positionRepository.findById(request.positionId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy chức vụ!"));

        // Thông thường không cho đổi nhân viên, chỉ đổi chức vụ, note hoặc thời gian
        ep.setPosition(pos);
        ep.setDescription(request.description());
        ep.setNote(request.note());
        ep.setStartDate(request.startDate());
        ep.setEndDate(request.endDate());

        EmployeePosition updatedEp = employeePositionRepository.save(ep);
        return mapToResponse(updatedEp);
    }

    // 4. Xóa mềm bản ghi
    public void deleteAssignment(UUID id) {
        EmployeePosition ep = employeePositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy bản ghi phân công!"));

        ep.setIsActive(false);
        ep.setDeletedAt(LocalDateTime.now());
        employeePositionRepository.save(ep);
    }

    // Helper: Map Entity -> DTO
    private EmployeePositionResponse mapToResponse(EmployeePosition ep) {
        return new EmployeePositionResponse(
                ep.getId(),
                ep.getEmployee().getId(),
                ep.getEmployee().getFullName(),
                ep.getEmployee().getCode(),
                ep.getPosition().getId(),
                ep.getPosition().getName(),
                ep.getPosition().getDepartment() != null ? ep.getPosition().getDepartment().getName() : "",
                ep.getDescription(),
                ep.getNote(),
                ep.getStartDate(),
                ep.getEndDate(),
                ep.getIsActive());
    }
}