package com.example.nhom3.position.controller;

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

import com.example.nhom3.position.model.dto.PositionRequest;
import com.example.nhom3.position.model.dto.PositionResponse;
import com.example.nhom3.position.service.PositionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/positions")
@CrossOrigin("*")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping
    public ResponseEntity<List<PositionResponse>> getAll() {
        return ResponseEntity.ok(positionService.getAllPositions());
    }

    // API hỗ trợ lấy danh sách chức vụ theo Khoa (dùng cho Dropdown đổ dữ liệu)
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<PositionResponse>> getByDepartment(@PathVariable UUID departmentId) {
        return ResponseEntity.ok(positionService.getPositionsByDepartment(departmentId));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PositionRequest request) {
        try {
            PositionResponse response = positionService.createPosition(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody PositionRequest request) {
        try {
            PositionResponse response = positionService.updatePosition(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            positionService.deletePosition(id);
            return ResponseEntity.ok("Xóa chức vụ thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}