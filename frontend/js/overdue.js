/**
 * Overdue Books Module — Phase 7
 * Loads overdue data from GET /api/overdue and GET /api/overdue/search
 * Fine and overdue days come from the backend — no independent JS calculation.
 */

const OVERDUE_API = CONFIG.API_BASE_URL + '/overdue';

let searchTimeout = null;

// ─── Initialise ───────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    loadAllOverdue();

    document.getElementById('searchInput').addEventListener('input', () => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(doSearch, 350);
    });
});

// ─── Load all overdue records ─────────────────────────────────────────────────
async function loadAllOverdue() {
    try {
        const res  = await fetch(OVERDUE_API);
        const json = await res.json();
        if (!json.success) throw new Error(json.message || 'Failed to load overdue records');
        renderTable(json.data || []);
    } catch (err) {
        console.error('loadAllOverdue error:', err);
        showTableError(err.message);
    }
}

// ─── Search ───────────────────────────────────────────────────────────────────
async function doSearch() {
    const keyword = document.getElementById('searchInput').value.trim();
    try {
        const url  = `${OVERDUE_API}/search?keyword=${encodeURIComponent(keyword)}`;
        const res  = await fetch(url);
        const json = await res.json();
        if (!json.success) throw new Error(json.message || 'Search failed');
        renderTable(json.data || []);
    } catch (err) {
        console.error('doSearch error:', err);
        showTableError(err.message);
    }
}

function clearSearch() {
    document.getElementById('searchInput').value = '';
    loadAllOverdue();
}

// ─── Render table ─────────────────────────────────────────────────────────────
function renderTable(records) {
    const tbody = document.getElementById('overdueTableBody');
    const tableCount  = document.getElementById('tableCount');
    const overdueCount = document.getElementById('overdueCount');
    const totalFineEl  = document.getElementById('totalFine');

    // Update summary cards
    overdueCount.textContent = records.length;
    tableCount.textContent   = `${records.length} overdue`;

    // Sum total fine from backend values
    const totalFine = records.reduce((sum, r) => sum + parseFloat(r.fine || 0), 0);
    totalFineEl.textContent = `₹${totalFine.toFixed(2)}`;

    // Empty state
    if (records.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center py-5">
                    <i class="bi bi-check-circle-fill text-success fs-1 d-block mb-2"></i>
                    <strong class="text-success">No overdue books found.</strong><br>
                    <small class="text-muted">All issued books are within their due dates.</small>
                </td>
            </tr>`;
        return;
    }

    tbody.innerHTML = records.map(r => {
        const days     = r.overdueDays;
        const fine     = parseFloat(r.fine || 0);
        const daysBadge = days > 30
            ? `<span class="badge bg-danger">${days} days</span>`
            : days > 14
            ? `<span class="badge bg-warning text-dark">${days} days</span>`
            : `<span class="badge bg-secondary">${days} days</span>`;

        return `<tr>
            <td><span class="badge bg-secondary">#${r.issueId}</span></td>
            <td>${escapeHtml(r.bookTitle)}</td>
            <td><small class="text-muted">${escapeHtml(r.isbn)}</small></td>
            <td>
                ${escapeHtml(r.studentName)}<br>
                <small class="text-muted">${escapeHtml(r.studentDepartment || '')}</small>
            </td>
            <td>${formatDate(r.issueDate)}</td>
            <td class="text-danger fw-semibold">${formatDate(r.dueDate)}</td>
            <td>${daysBadge}</td>
            <td><span class="badge bg-danger fs-6">₹${fine.toFixed(2)}</span></td>
        </tr>`;
    }).join('');
}

// ─── Error state ──────────────────────────────────────────────────────────────
function showTableError(message) {
    document.getElementById('overdueTableBody').innerHTML =
        `<tr><td colspan="8" class="text-center text-danger py-4">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            Error loading records: ${escapeHtml(message)}
        </td></tr>`;
    document.getElementById('tableCount').textContent = 'Error';
    document.getElementById('overdueCount').textContent = '—';
    document.getElementById('totalFine').textContent = '—';
}

// ─── Utilities ────────────────────────────────────────────────────────────────
function formatDate(iso) {
    if (!iso) return '—';
    const [y, m, d] = iso.split('-');
    return `${d}/${m}/${y}`;
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function showAlert(message, type = 'info') {
    const area = document.getElementById('alertArea');
    const id   = 'alert-' + Date.now();
    area.insertAdjacentHTML('beforeend', `
        <div id="${id}" class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${escapeHtml(message)}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`);
    setTimeout(() => { const el = document.getElementById(id); if (el) el.remove(); }, 6000);
}
