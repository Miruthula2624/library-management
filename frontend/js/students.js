/**
 * Students Module - College Library Management System
 * Full Student CRUD, multi-field search, registration validation, and error handling.
 */

let deleteModalInstance = null;
let currentDeleteId = null;

document.addEventListener('DOMContentLoaded', () => {
    const modalEl = document.getElementById('deleteModal');
    if (modalEl) {
        deleteModalInstance = new bootstrap.Modal(modalEl);
    }
    loadStudents();
});

/**
 * Display alert banner message
 */
function showAlert(message, type = 'success') {
    const banner = document.getElementById('alertBanner');
    const messageEl = document.getElementById('alertMessage');
    
    banner.className = `alert alert-${type} alert-dismissible fade show`;
    messageEl.textContent = message;
    banner.classList.remove('d-none');

    // Auto dismiss after 5 seconds
    setTimeout(() => {
        closeAlert();
    }, 5000);
}

function closeAlert() {
    const banner = document.getElementById('alertBanner');
    if (banner) {
        banner.classList.add('d-none');
    }
}

/**
 * Load and render all students
 */
async function loadStudents() {
    const tbody = document.getElementById('studentsTableBody');
    const countBadge = document.getElementById('studentCountBadge');

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/students`);
        if (!response.ok) {
            throw new Error(`Failed to load students (Status: ${response.status})`);
        }
        const students = await response.json();
        renderStudentsTable(students);
    } catch (error) {
        console.error('Error loading students:', error);
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center py-4 text-danger">
                    <i class="bi bi-exclamation-octagon fs-4 d-block mb-2"></i>
                    Unable to connect to the backend server at <code>${CONFIG.API_BASE_URL}/students</code>.<br>
                    <small class="text-muted">Ensure Spring Boot is running and database is accessible.</small>
                </td>
            </tr>
        `;
        if (countBadge) countBadge.textContent = '0 Students';
    }
}

/**
 * Render table rows
 */
function renderStudentsTable(students) {
    const tbody = document.getElementById('studentsTableBody');
    const countBadge = document.getElementById('studentCountBadge');

    if (countBadge) {
        countBadge.textContent = `${students.length} Student${students.length === 1 ? '' : 's'}`;
    }

    if (!students || students.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center py-4 text-muted">
                    <i class="bi bi-inbox fs-3 d-block mb-1"></i>
                    No students registered yet.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = students.map(student => {
        return `
            <tr>
                <td class="fw-semibold text-muted">#${student.studentId}</td>
                <td class="fw-bold">${escapeHtml(student.name)}</td>
                <td><span class="badge bg-light text-dark border">${escapeHtml(student.department)}</span></td>
                <td>${escapeHtml(student.year)}</td>
                <td><a href="mailto:${escapeHtml(student.email)}" class="text-decoration-none">${escapeHtml(student.email)}</a></td>
                <td>${escapeHtml(student.phone || '-')}</td>
                <td class="text-center">
                    <button class="btn btn-sm btn-outline-primary me-1" title="Edit" onclick="editStudent(${student.studentId})">
                        <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" title="Delete" onclick="openDeleteModal(${student.studentId}, '${escapeHtml(student.name).replace(/'/g, "\\'")}')">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Handle Search
 */
async function performSearch() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (!keyword) {
        loadStudents();
        return;
    }

    const tbody = document.getElementById('studentsTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="7" class="text-center py-4 text-muted">
                <div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>
                Searching students for "${escapeHtml(keyword)}"...
            </td>
        </tr>
    `;

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/students/search?keyword=${encodeURIComponent(keyword)}`);
        if (!response.ok) {
            throw new Error('Search failed');
        }
        const results = await response.json();
        renderStudentsTable(results);
    } catch (error) {
        console.error('Search error:', error);
        showAlert('Search failed: ' + error.message, 'danger');
    }
}

function handleSearchKeyPress(event) {
    if (event.key === 'Enter') {
        performSearch();
    }
}

function clearSearch() {
    document.getElementById('searchInput').value = '';
    loadStudents();
}

/**
 * Handle Form Submit (Add / Update)
 */
async function handleStudentSubmit(event) {
    event.preventDefault();

    const studentId = document.getElementById('studentId').value;
    const name = document.getElementById('studentName').value.trim();
    const email = document.getElementById('studentEmail').value.trim();
    const department = document.getElementById('studentDepartment').value.trim();
    const year = document.getElementById('studentYear').value.trim();
    const phone = document.getElementById('studentPhone').value.trim();

    if (!name || !email || !department || !year) {
        showAlert('Please fill in all required fields.', 'danger');
        return;
    }

    const payload = {
        name,
        email,
        department,
        year,
        phone: phone || null
    };

    const isEdit = Boolean(studentId);
    const url = isEdit ? `${CONFIG.API_BASE_URL}/students/${studentId}` : `${CONFIG.API_BASE_URL}/students`;
    const method = isEdit ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            const errorMsg = data.message || (isEdit ? 'Failed to update student.' : 'Failed to register student.');
            showAlert(errorMsg, 'danger');
            return;
        }

        showAlert(isEdit ? `Student "${name}" updated successfully.` : `Student "${name}" registered successfully.`, 'success');
        resetForm();
        loadStudents();
    } catch (error) {
        console.error('Error saving student:', error);
        showAlert('Network error: Could not connect to the server.', 'danger');
    }
}

/**
 * Edit Student - Load details into form
 */
async function editStudent(id) {
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/students/${id}`);
        if (!response.ok) {
            throw new Error('Could not fetch student details.');
        }
        const student = await response.json();

        document.getElementById('studentId').value = student.studentId;
        document.getElementById('studentName').value = student.name;
        document.getElementById('studentEmail').value = student.email;
        document.getElementById('studentDepartment').value = student.department;
        document.getElementById('studentYear').value = student.year;
        document.getElementById('studentPhone').value = student.phone || '';

        // Update UI state to Edit Mode
        document.getElementById('formTitle').innerHTML = '<i class="bi bi-pencil-square me-2 text-warning"></i>Edit Student';
        document.getElementById('editBadge').classList.remove('d-none');
        document.getElementById('saveBtn').innerHTML = '<i class="bi bi-arrow-repeat me-1"></i> Update Student';

        window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (error) {
        console.error('Error loading student for edit:', error);
        showAlert(error.message, 'danger');
    }
}

/**
 * Reset form back to Add mode
 */
function resetForm() {
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = '';
    document.getElementById('formTitle').innerHTML = '<i class="bi bi-person-plus me-2 text-primary"></i>Add New Student';
    document.getElementById('editBadge').classList.add('d-none');
    document.getElementById('saveBtn').innerHTML = '<i class="bi bi-check-lg me-1"></i> Add Student';
}

/**
 * Delete confirmation & execution
 */
function openDeleteModal(id, name) {
    currentDeleteId = id;
    document.getElementById('deleteStudentName').textContent = `"${name}"`;
    if (deleteModalInstance) {
        deleteModalInstance.show();
    }
}

async function executeDelete() {
    if (!currentDeleteId) return;

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/students/${currentDeleteId}`, {
            method: 'DELETE'
        });

        const data = await response.json();

        if (deleteModalInstance) {
            deleteModalInstance.hide();
        }

        if (!response.ok) {
            showAlert(data.message || 'Failed to delete student.', 'danger');
            return;
        }

        showAlert('Student record deleted successfully.', 'success');
        loadStudents();
    } catch (error) {
        console.error('Error deleting student:', error);
        if (deleteModalInstance) {
            deleteModalInstance.hide();
        }
        showAlert('Network error: Could not complete deletion.', 'danger');
    } finally {
        currentDeleteId = null;
    }
}

/**
 * Helper to escape HTML characters
 */
function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
