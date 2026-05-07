const API_URL = 'http://localhost:8081/api/employee-positions';
const EMP_API = 'http://localhost:8081/api/employees';
const POS_API = 'http://localhost:8081/api/positions';

document.addEventListener('DOMContentLoaded', () => {
    loadAssignData();
    loadDropdowns();
});

// 1. Tải dữ liệu vào bảng
async function loadAssignData() {
    try {
        const res = await fetch(API_URL);
        const data = await res.json();
        const tbody = document.getElementById('assign-body');
        tbody.innerHTML = '';

        data.forEach((item, index) => {
            const statusHtml = item.isActive 
                ? `<span class="bg-green-100 text-green-700 px-2 py-1 rounded text-xs font-bold">Đang hiệu lực</span>`
                : `<span class="bg-gray-100 text-gray-600 px-2 py-1 rounded text-xs font-bold">Hết hiệu lực</span>`;

            const start = item.startDate ? new Date(item.startDate).toLocaleDateString('vi-VN') : '---';
            const end = item.endDate ? new Date(item.endDate).toLocaleDateString('vi-VN') : 'Nay';

            tbody.innerHTML += `
                <tr class="border-b hover:bg-gray-50 transition">
                    <td class="p-3 text-center">${index + 1}</td>
                    <td class="p-3 font-medium text-blue-600">${item.employeeName || 'N/A'}</td> 
                    <td class="p-3 font-medium">${item.positionName || 'N/A'}</td> 
                    <td class="p-3 text-xs">${start} - ${end}</td>
                    <td class="p-3 text-xs text-gray-500">${item.note || ''}</td>
                    <td class="p-3 text-center">${statusHtml}</td>
                    <td class="p-3 text-center flex justify-center gap-2">
                        <button onclick="editAssign('${item.id}')" class="text-blue-600 hover:text-blue-800"><i class="fa-solid fa-pen-to-square"></i></button>
                        <button onclick="deleteAssign('${item.id}')" class="text-red-600 hover:text-red-800"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>`;
        });
    } catch (error) {
        console.error('Lỗi khi tải dữ liệu phân công:', error);
    }
}

// 2. Tải danh sách dropdown (Sửa lỗi undefined nhân viên)
async function loadDropdowns() {
    try {
        const [empRes, posRes] = await Promise.all([ fetch(EMP_API), fetch(POS_API) ]);
        const emps = await empRes.json();
        const positions = await posRes.json();

        const empSelect = document.getElementById('employeeId');
        empSelect.innerHTML = '<option value="">-- Chọn nhân viên --</option>';
        emps.forEach(e => {
            // Kiểm tra cả 2 trường hợp fullName hoặc full_name
            const name = e.fullName || e.full_name || 'Không rõ tên';
            empSelect.innerHTML += `<option value="${e.id}">${e.code || ''} - ${name}</option>`;
        });

        const posSelect = document.getElementById('positionId');
        posSelect.innerHTML = '<option value="">-- Chọn chức vụ --</option>';
        positions.forEach(p => {
            posSelect.innerHTML += `<option value="${p.id}">${p.name}</option>`;
        });
    } catch (error) {
        console.error('Lỗi khi tải dropdown:', error);
    }
}

// 3. Hàm Đổ dữ liệu vào Modal khi nhấn Sửa
async function editAssign(id) {
    try {
        const res = await fetch(API_URL);
        const data = await res.json();
        const item = data.find(x => x.id === id);
        
        if (item) {
            document.getElementById('assignId').value = item.id;
            document.getElementById('employeeId').value = item.employeeId;
            document.getElementById('positionId').value = item.positionId;
            
            if(item.startDate) document.getElementById('startDate').value = item.startDate.split('T')[0];
            if(item.endDate) document.getElementById('endDate').value = item.endDate.split('T')[0];
            
            document.getElementById('description').value = item.description || '';
            document.getElementById('note').value = item.note || '';
            document.getElementById('isActive').value = String(item.isActive);
            
            document.getElementById('modalTitle').innerText = 'Chỉnh Sửa Phân Công';
            document.getElementById('assignModal').classList.remove('hidden');
        }
    } catch (e) { alert('Không thể lấy thông tin sửa'); }
}

// 4. Lưu dữ liệu (Giữ nguyên logic của bạn nhưng bọc try-catch tốt hơn)
document.getElementById('assignForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('assignId').value;
    const payload = {
        employeeId: document.getElementById('employeeId').value,
        positionId: document.getElementById('positionId').value,
        startDate: document.getElementById('startDate').value,
        endDate: document.getElementById('endDate').value || null,
        description: document.getElementById('description').value,
        note: document.getElementById('note').value,
        isActive: document.getElementById('isActive').value === 'true'
    };

    try {
        const response = await fetch(id ? `${API_URL}/${id}` : API_URL, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if(response.ok) {
            closeAssignModal();
            loadAssignData();
        } else { alert('Lỗi khi lưu dữ liệu!'); }
    } catch (error) { console.error('Lỗi:', error); }
});

async function deleteAssign(id) {
    if(confirm('Xóa bản ghi này?')) {
        await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        loadAssignData();
    }
}