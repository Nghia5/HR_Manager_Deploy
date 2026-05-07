package com.example.nhom3.employeeposition.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nhom3.employeeposition.model.dto.EmployeePositionRequest;
import com.example.nhom3.employeeposition.model.dto.EmployeePositionResponse;
import com.example.nhom3.employeeposition.service.EmployeePositionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employee-positions")
@CrossOrigin("*")
public class EmployeePositionController {

    @Autowired
    private EmployeePositionService employeePositionService;

    @GetMapping("/all")
    public ResponseEntity<List<EmployeePositionResponse>> getAll() {
        // Chúng ta sẽ tận dụng hàm lấy tất cả các bản ghi đang active
        return ResponseEntity.ok(employeePositionService.getAllAssignments());
    }

    // Lấy lịch sử công tác của 1 nhân viên cụ thể
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeePositionResponse>> getByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employeePositionService.getHistoryByEmployee(employeeId));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EmployeePositionRequest request) {
        try {
            EmployeePositionResponse response = employeePositionService.createAssignment(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody EmployeePositionRequest request) {
        try {
            EmployeePositionResponse response = employeePositionService.updateAssignment(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            employeePositionService.deleteAssignment(id);
            return ResponseEntity.ok("Đã xóa bản ghi phân công công việc!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}