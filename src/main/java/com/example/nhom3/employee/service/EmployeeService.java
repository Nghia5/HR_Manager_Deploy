package com.example.nhom3.employee.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.nhom3.employee.model.dto.EmployeeRequest;
import com.example.nhom3.employee.model.dto.EmployeeResponse;
import com.example.nhom3.department.model.entity.Department;
import com.example.nhom3.employee.model.entity.Employee;
import com.example.nhom3.position.model.entity.Position;
import com.example.nhom3.user.model.entity.User;
import com.example.nhom3.employee.repository.EmployeeRepository;
import com.example.nhom3.department.repository.DepartmentRepository;
import com.example.nhom3.position.repository.PositionRepository;
import com.example.nhom3.user.repository.UserRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private UserRepository userRepository;

    // 1. LẤY TẤT CẢ NHÂN VIÊN
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 2. LẤY NHÂN VIÊN THEO KHOA
    public List<EmployeeResponse> getEmployeesByDepartment(UUID departmentId) {
        return employeeRepository.findByDepartmentIdAndIsActiveTrue(departmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 3. THÊM MỚI NHÂN VIÊN
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // Validate dữ liệu trùng lặp
        if (employeeRepository.existsByCode(request.code()))
            throw new RuntimeException("Lỗi: Mã nhân viên đã tồn tại!");
        if (employeeRepository.existsByEmail(request.email()))
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");

        Employee emp = new Employee();
        mapRequestToEntity(request, emp);

        Employee savedEmp = employeeRepository.save(emp);
        return mapToResponse(savedEmp);
    }

    // 4. CẬP NHẬT NHÂN VIÊN
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy nhân viên!"));

        // Validate nếu có thay đổi Mã NV hoặc Email
        if (!emp.getCode().equals(request.code()) && employeeRepository.existsByCode(request.code())) {
            throw new RuntimeException("Lỗi: Mã nhân viên mới đã tồn tại!");
        }
        if (!emp.getEmail().equals(request.email()) && employeeRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Lỗi: Email mới đã được sử dụng!");
        }

        mapRequestToEntity(request, emp);

        Employee updatedEmp = employeeRepository.save(emp);
        return mapToResponse(updatedEmp);
    }

    // 5. XÓA MỀM (Nghỉ việc)
    public void deleteEmployee(UUID id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy nhân viên!"));

        emp.setIsActive(false);
        emp.setDeletedAt(LocalDateTime.now());
        // Có thể bổ sung logic khóa tài khoản User ở đây nếu cần

        employeeRepository.save(emp);
    }

    // --- CÁC HÀM HELPER HỖ TRỢ CHUYỂN ĐỔI DỮ LIỆU ---

    private void mapRequestToEntity(EmployeeRequest request, Employee emp) {
        emp.setCode(request.code());
        emp.setFullName(request.fullName());
        emp.setDateOfBirth(request.dateOfBirth());
        emp.setGender(request.gender());
        emp.setEmail(request.email());
        emp.setPhone(request.phone());
        emp.setAddress(request.address());
        emp.setHireDate(request.hireDate());
        emp.setContractType(request.contractType());
        emp.setSalaryCoefficient(request.salaryCoefficient());
        emp.setAcademicDegree(request.academicDegree());
        emp.setAcademicTitle(request.academicTitle());
        emp.setSpecialization(request.specialization());
        emp.setStartDate(request.startDate());
        emp.setEndDate(request.endDate());

        // Xử lý các Khóa ngoại (Bắt buộc phải có Khoa và Chức vụ)
        Department dept = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Khoa/Phòng ban không tồn tại!"));
        emp.setDepartment(dept);

        Position pos = positionRepository.findById(request.positionId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Chức vụ không tồn tại!"));

        // Ràng buộc nghiệp vụ: Chức vụ phải thuộc về Khoa đã chọn
        if (!pos.getDepartment().getId().equals(dept.getId())) {
            throw new RuntimeException("Lỗi: Chức vụ này không thuộc về Khoa/Phòng ban đã chọn!");
        }
        emp.setPosition(pos);

        // Xử lý Tài khoản User (Có thể null nếu nhân viên chưa được cấp tài khoản)
        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Tài khoản User không tồn tại!"));
            emp.setUser(user);
        } else {
            emp.setUser(null);
        }
    }

    private EmployeeResponse mapToResponse(Employee emp) {
        return new EmployeeResponse(
                emp.getId(),
                emp.getCode(),
                emp.getFullName(),
                emp.getDateOfBirth(),
                emp.getGender(),
                emp.getEmail(),
                emp.getPhone(),
                emp.getAddress(),
                emp.getDepartment() != null ? emp.getDepartment().getId() : null,
                emp.getDepartment() != null ? emp.getDepartment().getName() : "Chưa xếp khoa",
                emp.getPosition() != null ? emp.getPosition().getId() : null,
                emp.getPosition() != null ? emp.getPosition().getName() : "Chưa có chức vụ",
                emp.getUser() != null ? emp.getUser().getId() : null,
                emp.getUser() != null ? emp.getUser().getUsername() : "Chưa cấp tài khoản",
                emp.getHireDate(),
                emp.getContractType(),
                emp.getSalaryCoefficient(),
                emp.getAcademicDegree(),
                emp.getAcademicTitle(),
                emp.getSpecialization(),
                emp.getStartDate(),
                emp.getEndDate(),
                emp.getIsActive());
    }
}