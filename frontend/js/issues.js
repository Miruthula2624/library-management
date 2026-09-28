/**
 * Issue Management Module - College Library Management System
 * Book issuing, availability checks, stock decrement sync, and transactions table.
 */

let booksCache = [];

document.addEventListener('DOMContentLoaded', () => {
    initDateDefaults();
    loadBooksForDropdown();
    loadStudentsForDropdown();
    loadIssues();
});

/**
 * Initialize default dates: Issue Date = Today, Due Date = Today + 14 days
 */
function initDateDefaults() {
    const today = new Date();
    const issueDateEl = document.getElementById('issueDate');
    const dueDateEl = document.getElementById('dueDate');

    const issueDateStr = formatDateForInput(today);
    const dueDateObj = new Date(today);
    dueDateObj.setDate(dueDateObj.getDate() + 14);
    const dueDateStr = formatDateForInput(dueDateObj);

    if (issueDateEl) {
        issueDateEl.value = issueDateStr;
        handleIssueDateChange();
    }
    if (dueDateEl) {
        dueDateEl.value = dueDateStr;
    }
}

function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function handleIssueDateChange() {
    const issueDateVal = document.getElementById('issueDate').value;
    const dueDateEl = document.getElementById('dueDate');
    if (issueDateVal && dueDateEl) {
        const nextDay = new Date(issueDateVal);
        nextDay.setDate(nextDay.getDate() + 1);
        dueDateEl.min = formatDateForInput(nextDay);
    }
}

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
 * Load Books for Dropdown
 */
async function loadBooksForDropdown() {
    const select = document.getElementById('bookSelect');
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/books`);
        if (!response.ok) throw new Error('Failed to load books');
        booksCache = await response.json();

        select.innerHTML = '<option value="">-- Choose Book --</option>';
        booksCache.forEach(book => {
            const isAvailable = Number(book.availableCopies) > 0;
            const disabledAttr = isAvailable ? '' : 'disabled';
            const statusLabel = isAvailable ? `${book.availableCopies} available` : 'UNAVAILABLE';
            
            select.innerHTML += `
                <option value="${book.bookId}" data-available="${book.availableCopies}" ${disabledAttr}>
                    ${escapeHtml(book.title)} - ${escapeHtml(book.author)} (${statusLabel})
                </option>
            `;
        });
    } catch (error) {
        console.error('Error loading books dropdown:', error);
        select.innerHTML = '<option value="">Error loading books</option>';
    }
}

/**
 * Load Students for Dropdown
 */
async function loadStudentsForDropdown() {
    const select = document.getElementById('studentSelect');
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/students`);
        if (!response.ok) throw new Error('Failed to load students');
        const students = await response.json();

        select.innerHTML = '<option value="">-- Choose Student --</option>';
        students.forEach(student => {
            select.innerHTML += `
                <option value="${student.studentId}">
                    ${escapeHtml(student.name)} - ${escapeHtml(student.department)} (ID: #${student.studentId})
                </option>
            `;
        });
    } catch (error) {
        console.error('Error loading students dropdown:', error);
        select.innerHTML = '<option value="">Error loading students</option>';
    }
}

function handleBookSelectChange() {
    const select = document.getElementById('bookSelect');
    const notice = document.getElementById('bookAvailabilityNotice');
    const selectedOption = select.options[select.selectedIndex];
    
    if (!selectedOption || !selectedOption.value) {
        notice.innerHTML = '';
        return;
    }

    const available = parseInt(selectedOption.getAttribute('data-available'), 10);
    if (available <= 0) {
        notice.innerHTML = '<span class="text-danger fw-semibold"><i class="bi bi-x-circle me-1"></i> This book is currently unavailable.</span>';
    } else {
        notice.innerHTML = `<span class="text-success"><i class="bi bi-check-circle me-1"></i> Copies currently available: ${available}</span>`;
    }
}

/**
 * Load All Issue Transactions
 */
async function loadIssues() {
    const tbody = document.getElementById('issuesTableBody');
    const countBadge = document.getElementById('issueCountBadge');

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/issues`);
        if (!response.ok) throw new Error('Failed to load issues');
        const issues = await response.json();
        renderIssuesTable(issues);
    } catch (error) {
        console.error('Error loading issues:', error);
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-4 text-danger">
                    <i class="bi bi-exclamation-octagon fs-4 d-block mb-2"></i>
                    Unable to connect to backend server at <code>${CONFIG.API_BASE_URL}/issues</code>.<br>
                    <small class="text-muted">Ensure Spring Boot is running and database is accessible.</small>
                </td>
            </tr>
        `;
        if (countBadge) countBadge.textContent = '0 Transactions';
    }
}

/**
 * Render Issues Table
 */
function renderIssuesTable(issues) {
    const tbody = document.getElementById('issuesTableBody');
    const countBadge = document.getElementById('issueCountBadge');

    if (countBadge) {
        countBadge.textContent = `${issues.length} Transaction${issues.length === 1 ? '' : 's'}`;
    }

    if (!issues || issues.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-4 text-muted">
                    <i class="bi bi-inbox fs-3 d-block mb-1"></i>
                    No issue records found.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = issues.map(issue => {
        let statusBadge = '<span class="badge bg-primary">Active</span>';
        if (issue.status === 'Returned') {
            statusBadge = '<span class="badge bg-secondary">Returned</span>';
        } else if (issue.status === 'Overdue') {
            statusBadge = '<span class="badge bg-danger">Overdue</span>';
        }

        return `
            <tr>
                <td class="fw-semibold text-muted">#${issue.issueId}</td>
                <td>
                    <div class="fw-bold">${escapeHtml(issue.bookTitle)}</div>
                    <small class="text-muted">ISBN: <code>${escapeHtml(issue.bookIsbn)}</code></small>
                </td>
                <td>
                    <div class="fw-semibold">${escapeHtml(issue.studentName)}</div>
                    <small class="text-muted">${escapeHtml(issue.studentDepartment)} (#${issue.studentId})</small>
                </td>
                <td>${escapeHtml(issue.issueDate)}</td>
                <td>${escapeHtml(issue.dueDate)}</td>
                <td class="text-center">${statusBadge}</td>
            </tr>
        `;
    }).join('');
}

/**
 * Handle Issue Form Submit
 */
async function handleIssueSubmit(event) {
    event.preventDefault();

    const bookId = document.getElementById('bookSelect').value;
    const studentId = document.getElementById('studentSelect').value;
    const issueDate = document.getElementById('issueDate').value;
    const dueDate = document.getElementById('dueDate').value;

    if (!bookId || !studentId || !issueDate || !dueDate) {
        showAlert('Please fill in all required fields.', 'danger');
        return;
    }

    if (dueDate <= issueDate) {
        showAlert('Due date must be after the issue date.', 'danger');
        return;
    }

    const payload = {
        bookId: Number(bookId),
        studentId: Number(studentId),
        issueDate: issueDate,
        dueDate: dueDate
    };

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/issues`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            showAlert(data.message || 'Failed to issue book.', 'danger');
            return;
        }

        showAlert(`Book successfully issued to student (Issue #${data.issueId || ''}).`, 'success');
        resetIssueForm();
        loadIssues();
        loadBooksForDropdown(); // Refresh dropdown copy counts
    } catch (error) {
        console.error('Error issuing book:', error);
        showAlert('Network error: Could not complete transaction.', 'danger');
    }
}

/**
 * Handle Search
 */
async function performSearch() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (!keyword) {
        loadIssues();
        return;
    }

    const tbody = document.getElementById('issuesTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="6" class="text-center py-4 text-muted">
                <div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>
                Searching transactions for "${escapeHtml(keyword)}"...
            </td>
        </tr>
    `;

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/issues/search?keyword=${encodeURIComponent(keyword)}`);
        if (!response.ok) throw new Error('Search failed');
        const results = await response.json();
        renderIssuesTable(results);
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
    loadIssues();
}

/**
 * Reset Issue Form
 */
function resetIssueForm() {
    document.getElementById('issueForm').reset();
    document.getElementById('bookAvailabilityNotice').innerHTML = '';
    initDateDefaults();
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
