document.addEventListener("DOMContentLoaded", fetchDepartments);

function fetchDepartments() {
    fetch('/api/departments')
        .then(res => res.json())
        .then(data => {
            const body = document.getElementById('dept-body');
            if (!body) return;
            body.innerHTML = data.map((d, i) => `
                <tr class="border-b hover:bg-gray-50 text-sm">
                    <td class="p-3 text-center">${i+1}</td>
                    <td class="p-3 font-bold">${d.code}</td>
                    <td class="p-3">${d.name}</td>
                    <td class="p-3 text-gray-500">${d.description || ''}</td>
                    <td class="p-3 text-center">${d.isActive ? '✅' : '❌'}</td>
                    <td class="p-3 text-center">
                        <button onclick="editDept('${d.id}')" class="text-blue-500 mr-2"><i class="fa-solid fa-pen-to-square"></i></button>
                        <button onclick="deleteDept('${d.id}')" class="text-red-500"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `).join('');
        });
}

function openAddDeptModal() {
    document.getElementById('modalTitle').innerText = "Thêm Khoa Mới";
    document.getElementById('deptForm').reset();
    document.getElementById('deptId').value = '';
    document.getElementById('deptModal').classList.remove('hidden');
}

function closeDeptModal() { document.getElementById('deptModal').classList.add('hidden'); }

function editDept(id) {
    fetch(`/api/departments/${id}`).then(res => res.json()).then(data => {
        document.getElementById('modalTitle').innerText = "Chỉnh sửa Khoa";
        document.getElementById('deptId').value = data.id;
        document.getElementById('code').value = data.code;
        document.getElementById('name').value = data.name;
        document.getElementById('description').value = data.description;
        document.getElementById('isActive').value = data.isActive.toString();
        document.getElementById('deptModal').classList.remove('hidden');
    });
}

document.getElementById('deptForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const id = document.getElementById('deptId').value;
    const data = {
        code: document.getElementById('code').value,
        name: document.getElementById('name').value,
        description: document.getElementById('description').value,
        isActive: document.getElementById('isActive').value === 'true'
    };
    fetch(id ? `/api/departments/${id}` : '/api/departments', {
        method: id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(res => { if(res.ok) { closeDeptModal(); fetchDepartments(); } });
});

function deleteDept(id) {
    if(confirm("Xóa khoa này?")) {
        fetch('/api/departments/' + id, { method: 'DELETE' })
        .then(res => res.ok ? fetchDepartments() : alert("Lỗi xóa!"));
    }
}