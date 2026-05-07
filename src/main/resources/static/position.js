document.addEventListener("DOMContentLoaded", fetchPositions);

function fetchPositions() {
    fetch('/api/positions')
        .then(res => res.json())
        .then(data => {
            const body = document.getElementById('pos-body');
            if (!body) return;
            body.innerHTML = data.map((p, i) => `
                <tr class="border-b hover:bg-gray-50 text-sm">
                    <td class="p-3 text-center">${i+1}</td>
                    <td class="p-3 font-bold">${p.code}</td>
                    <td class="p-3">${p.name}</td>
                    <td class="p-3">${p.level || ''}</td>
                    <td class="p-3">${p.department ? p.department.name : '-'}</td>
                    <td class="p-3 text-center">${p.isActive ? '✅' : '❌'}</td>
                    <td class="p-3 text-center">
                        <button onclick="editPos('${p.id}')" class="text-blue-500 mr-2"><i class="fa-solid fa-pen-to-square"></i></button>
                        <button onclick="deletePos('${p.id}')" class="text-red-500"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        });
}

function openAddPosModal() {
    document.getElementById('posModalTitle').innerText = "Thêm Chức Vụ Mới";
    document.getElementById('posForm').reset();
    document.getElementById('posId').value = '';
    loadDeptSelect();
    document.getElementById('posModal').classList.remove('hidden');
}

function closePosModal() { document.getElementById('posModal').classList.add('hidden'); }

function loadDeptSelect() {
    fetch('/api/departments')
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('posDeptId');
            // Thêm lựa chọn mặc định vào đầu
            let options = '<option value="">-- Chọn Khoa / Phòng ban --</option>';
            
            // Duyệt qua dữ liệu từ API
            options += data.map(d => `<option value="${d.id}">${d.name}</option>`).join('');
            
            select.innerHTML = options;
        });
}

function editPos(id) {
    fetch(`/api/positions/${id}`).then(res => res.json()).then(data => {
        document.getElementById('posModalTitle').innerText = "Chỉnh sửa Chức vụ";
        document.getElementById('posId').value = data.id;
        document.getElementById('posCode').value = data.code;
        document.getElementById('posName').value = data.name;
        document.getElementById('posLevel').value = data.level;
        document.getElementById('posIsActive').value = data.isActive.toString();
        loadDeptSelect();
        setTimeout(() => { document.getElementById('posDeptId').value = data.department.id; }, 100);
        document.getElementById('posModal').classList.remove('hidden');
    });
}

document.getElementById('posForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const id = document.getElementById('posId').value;
    const data = {
        code: document.getElementById('posCode').value,
        name: document.getElementById('posName').value,
        level: document.getElementById('posLevel').value,
        isActive: document.getElementById('posIsActive').value === 'true',
        department: { id: document.getElementById('posDeptId').value }
    };
    fetch(id ? `/api/positions/${id}` : '/api/positions', {
        method: id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(res => { if(res.ok) { closePosModal(); fetchPositions(); } });
});

function deletePos(id) {
    if(confirm("Xóa chức vụ này?")) {
        fetch('/api/positions/' + id, { method: 'DELETE' })
        .then(res => res.ok ? fetchPositions() : alert("Lỗi xóa!"));
    }
}