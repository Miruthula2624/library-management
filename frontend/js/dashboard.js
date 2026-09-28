/**
 * Dashboard Module — Phase 8
 * Loads all library statistics from GET /api/dashboard
 */

const DASHBOARD_API = CONFIG.API_BASE_URL + '/dashboard';

// ─── Initialise ───────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    loadDashboard();
});

// ─── Load dashboard statistics ────────────────────────────────────────────────
async function loadDashboard() {
    const loadingEl = document.getElementById('loadingState');
    const gridEl    = document.getElementById('statsGrid');

    loadingEl.classList.remove('d-none');
    gridEl.classList.add('d-none');

    try {
        const res  = await fetch(DASHBOARD_API);
        const json = await res.json();

        if (!res.ok || !json.success) {
            throw new Error(json.message || 'Unable to load dashboard data.');
        }

        renderStats(json.data);
        loadingEl.classList.add('d-none');
        gridEl.classList.remove('d-none');

    } catch (err) {
        console.error('Dashboard load error:', err);
        loadingEl.innerHTML = `
            <i class="bi bi-exclamation-triangle-fill text-danger fs-1 d-block mb-2"></i>
            <p class="text-danger fw-semibold">Unable to load dashboard data.</p>
            <p class="text-muted small">${escapeHtml(err.message)}</p>
            <button class="btn btn-outline-primary btn-sm" onclick="loadDashboard()">
                <i class="bi bi-arrow-clockwise me-1"></i>Retry
            </button>`;
    }
}

// ─── Render statistics into the cards ─────────────────────────────────────────
function renderStats(data) {
    setText('statTotalBooks',       data.totalBooks      ?? 0);
    setText('statTotalStudents',    data.totalStudents   ?? 0);
    setText('statIssuedBooks',      data.totalIssuedBooks ?? 0);
    setText('statOverdueBooks',     data.totalOverdueBooks ?? 0);
    setText('statTotalCopies',      data.totalCopies     ?? 0);
    setText('statAvailableCopies',  data.availableCopies ?? 0);

    // Fine: backend sends BigDecimal as a JSON number/string
    const fine = parseFloat(data.totalOverdueFine ?? 0);
    setText('statOverdueFine', `₹${fine.toFixed(2)}`);
}

// ─── Utilities ────────────────────────────────────────────────────────────────
function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value;
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
