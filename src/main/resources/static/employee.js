document.addEventListener("DOMContentLoaded", () => {
    fetchEmployees();
});

// 1. Lấy danh sách nhân viên và hiển thị lên bảng
function fetchEmployees() {
    fetch('/api/employees')
        .then(res => res.json())
        .then(data => {
            const body = document.getElementById('emp-body');
            if (!body) return;
            body.innerHTML = data.map((e, i) => `
                <tr class="border-b hover:bg-gray-50 text-sm">
                    <td class="p-3 text-center">${i + 1}</td>
                    <td class="p-3 font-bold text-blue-600">${e.code}</td>
                    <td class="p-3 font-medium">${e.fullName}</td>
                    <td class="p-3 text-gray-600">${e.department ? e.department.name : '<span class="text-gray-400 italic text-xs">Chưa chọn</span>'}</td>
                    <td class="p-3 text-gray-600">${e.position ? e.position.name : '<span class="text-gray-400 italic text-xs">Chưa chọn</span>'}</td>
                    <td class="p-3 text-center text-lg">${e.isActive ? '✅' : '❌'}</td>
                    <td class="p-3 text-center">
                        <button onclick="editEmp('${e.id}')" class="text-blue-500 hover:text-blue-700 mr-3 transition"><i class="fa-solid fa-pen-to-square"></i></button>
                        <button onclick="deleteEmp('${e.id}')" class="text-red-500 hover:text-red-700 transition"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        })
        .catch(err => console.error("Lỗi khi tải nhân viên:", err));
}

// 2. Tải danh mục Khoa và Chức vụ vào Select box
async function loadDropdowns() {
    try {
        const [deptRes, posRes] = await Promise.all([
            fetch('/api/departments'),
            fetch('/api/positions')
        ]);
        
        const depts = await deptRes.json();
        const positions = await posRes.json();

        const deptSelect = document.getElementById('departmentId');
        const posSelect = document.getElementById('positionId');

        if(deptSelect) {
            deptSelect.innerHTML = '<option value="">-- Chọn Khoa/Viện --</option>' + 
                depts.map(d => `<option value="${d.id}">${d.name}</option>`).join('');
        }
            
        if(posSelect) {
            posSelect.innerHTML = '<option value="">-- Chọn Chức vụ --</option>' + 
                positions.map(p => `<option value="${p.id}">${p.name}</option>`).join('');
        }
    } catch (error) {
        console.error("Lỗi khi tải danh mục:", error);
    }
}

// 3. Mở Modal thêm mới
async function openAddEmpModal() {
    document.getElementById('modalTitle').innerText = "Thêm Nhân Viên Mới";
    document.getElementById('empForm').reset();
    document.getElementById('empId').value = '';
    
    // Xóa class lỗi hoặc định dạng cũ nếu có
    await loadDropdowns(); 
    document.getElementById('empModal').classList.remove('hidden');
}

// 4. Đóng Modal
function closeEmpModal() {
    document.getElementById('empModal').classList.add('hidden');
}

// 5. Chỉnh sửa: Đổ dữ liệu cũ vào đầy đủ các trường mới
async function editEmp(id) {
    try {
        const res = await fetch(`/api/employees/${id}`);
        if (!res.ok) throw new Error("Không tìm thấy nhân viên");
        const data = await res.json();
        
        await loadDropdowns();

        document.getElementById('modalTitle').innerText = "Chỉnh sửa Nhân viên";
        document.getElementById('empId').value = data.id;
        document.getElementById('code').value = data.code;
        document.getElementById('fullName').value = data.fullName;
        document.getElementById('dateOfBirth').value = data.dateOfBirth || '';
        document.getElementById('gender').value = data.gender !== null ? data.gender.toString() : "1";
        document.getElementById('salaryCoefficient').value = data.salaryCoefficient || '';
        document.getElementById('academicDegree').value = data.academicDegree || '';
        document.getElementById('specialization').value = data.specialization || '';
        document.getElementById('email').value = data.email || '';
        document.getElementById('phone').value = data.phone || '';
        document.getElementById('address').value = data.address || '';
        document.getElementById('isActive').value = data.isActive.toString();
        
        if (data.department) document.getElementById('departmentId').value = data.department.id;
        if (data.position) document.getElementById('positionId').value = data.position.id;

        document.getElementById('empModal').classList.remove('hidden');
    } catch (error) {
        alert("Lỗi: " + error.message);
    }
}

// 6. Submit Form: Đóng gói JSON gửi lên Server (Đồng bộ tuyệt đối với Java Entity)
document.getElementById('empForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const id = document.getElementById('empId').value;
    const deptId = document.getElementById('departmentId').value;
    const posId = document.getElementById('positionId').value;

    const data = {
        code: document.getElementById('code').value.trim(),
        fullName: document.getElementById('fullName').value.trim(),
        dateOfBirth: document.getElementById('dateOfBirth').value || null,
        gender: parseInt(document.getElementById('gender').value),
        salaryCoefficient: document.getElementById('salaryCoefficient').value ? parseFloat(document.getElementById('salaryCoefficient').value) : null,
        academicDegree: document.getElementById('academicDegree').value.trim() || null,
        specialization: document.getElementById('specialization').value.trim() || null,
        email: document.getElementById('email').value.trim() || null,
        phone: document.getElementById('phone').value.trim() || null,
        address: document.getElementById('address').value.trim() || null,
        isActive: document.getElementById('isActive').value === 'true',
        department: deptId ? { id: deptId } : null,
        position: posId ? { id: posId } : null
    };

    const url = id ? `/api/employees/${id}` : '/api/employees';
    const method = id ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(async res => {
        if (res.ok) {
            alert(id ? "Cập nhật thành công! 🎉" : "Thêm nhân viên thành công! 🎉");
            closeEmpModal();
            fetchEmployees(); 
        } else {
            const errorData = await res.json();
            // Xử lý báo lỗi cụ thể (ví dụ trùng Mã NV)
            alert("Lỗi server: " + (errorData.message || "Kiểm tra lại dữ liệu (Mã NV có thể đã tồn tại)"));
        }
    })
    .catch(err => {
        console.error("Lỗi kết nối:", err);
        alert("Không thể kết nối đến Server!");
    });
});

// 7. Xóa nhân viên
function deleteEmp(id) {
    if (confirm("⚠️ Bạn có chắc chắn muốn xóa nhân viên này? Hành động này không thể hoàn tác.")) {
        fetch('/api/employees/' + id, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    fetchEmployees();
                } else {
                    alert("Lỗi: Không thể xóa nhân viên này (có thể do ràng buộc dữ liệu)");
                }
            })
            .catch(err => alert("Lỗi kết nối khi xóa!"));
    }
}