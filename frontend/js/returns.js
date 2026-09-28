/**
 * Return Book Module — Phase 6
 * Handles: active issue dropdown, return form, fine preview, return records table, search
 */

const RETURNS_API = CONFIG.API_BASE_URL + '/returns';
const ISSUES_API  = CONFIG.API_BASE_URL + '/issues';

let activeIssues = [];      // unreturned issues loaded from server
let searchTimeout = null;

// ─── Initialise ──────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    setDefaultReturnDate();
    loadActiveIssues();
    loadAllReturns();

    // Dropdown change → show details + recalculate fine preview
    document.getElementById('issueSelect').addEventListener('change', onIssueSelected);

    // Return date change → recalculate fine preview
    document.getElementById('returnDate').addEventListener('change', onDateChanged);

    // Form submit
    document.getElementById('returnForm').addEventListener('submit', handleReturnSubmit);

    // Live search
    document.getElementById('searchInput').addEventListener('input', () => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(doSearch, 350);
    });
});

// ─── Date helpers ─────────────────────────────────────────────────────────────
function todayISO() {
    return new Date().toISOString().split('T')[0];
}

function setDefaultReturnDate() {
    document.getElementById('returnDate').value = todayISO();
}

function formatDate(iso) {
    if (!iso) return '—';
    const [y, m, d] = iso.split('-');
    return `${d}/${m}/${y}`;
}

// ─── Load active (unreturned) issues ─────────────────────────────────────────
async function loadActiveIssues() {
    try {
        const res = await fetch(ISSUES_API);
        const json = await res.json();
        if (!json.success) throw new Error(json.message || 'Failed to load issues');

        // Filter to only Active or Overdue (not returned)
        activeIssues = (json.data || []).filter(i => !i.returned);

        populateIssueDropdown(activeIssues);
    } catch (err) {
        console.error('loadActiveIssues error:', err);
        showAlert('Could not load active issues: ' + err.message, 'warning');
    }
}

function populateIssueDropdown(issues) {
    const sel = document.getElementById('issueSelect');
    const currentVal = sel.value;

    // Remove all but the placeholder
    while (sel.options.length > 1) sel.remove(1);

    if (issues.length === 0) {
        const opt = new Option('No active issues to return', '');
        opt.disabled = true;
        sel.add(opt);
        return;
    }

    issues.forEach(issue => {
        const label = `#${issue.issueId} — ${escapeHtml(issue.bookTitle)} → ${escapeHtml(issue.studentName)} (Due: ${formatDate(issue.dueDate)})`;
        const opt = new Option(label, issue.issueId);
        if (issue.status === 'Overdue') opt.style.color = '#dc3545';
        sel.add(opt);
    });

    // Restore selection if possible
    if (currentVal) sel.value = currentVal;
}

// ─── Issue selected ───────────────────────────────────────────────────────────
function onIssueSelected() {
    const issueId = parseInt(document.getElementById('issueSelect').value);
    if (!issueId) {
        hideIssueDetails();
        hideFinePreview();
        return;
    }
    const issue = activeIssues.find(i => i.issueId === issueId);
    if (!issue) return;

    // Show details panel
    document.getElementById('detailBook').textContent      = issue.bookTitle;
    document.getElementById('detailStudent').textContent   = issue.studentName;
    document.getElementById('detailIssueDate').textContent = formatDate(issue.issueDate);
    document.getElementById('detailDueDate').textContent   = formatDate(issue.dueDate);
    document.getElementById('issueDetails').classList.remove('d-none');

    updateFinePreview(issue.dueDate);
}

function onDateChanged() {
    const issueId = parseInt(document.getElementById('issueSelect').value);
    if (!issueId) return;
    const issue = activeIssues.find(i => i.issueId === issueId);
    if (!issue) return;
    updateFinePreview(issue.dueDate);
}

function hideIssueDetails() {
    document.getElementById('issueDetails').classList.add('d-none');
}

// ─── Fine preview (client-side estimate) ─────────────────────────────────────
function updateFinePreview(dueDateISO) {
    const returnDateVal = document.getElementById('returnDate').value || todayISO();
    const dueDate    = new Date(dueDateISO);
    const returnDate = new Date(returnDateVal);

    const diffMs   = returnDate - dueDate;
    const lateDays = Math.max(0, Math.floor(diffMs / (1000 * 60 * 60 * 24)));
    const fine     = lateDays * 5;

    const previewArea  = document.getElementById('finePreview');
    const previewAlert = document.getElementById('finePreviewAlert');
    const previewAmt   = document.getElementById('finePreviewAmount');

    previewArea.classList.remove('d-none');
    if (lateDays === 0) {
        previewAlert.className = 'alert alert-success mb-0 py-2';
        previewAmt.textContent = '₹0.00 (on time)';
    } else {
        previewAlert.className = 'alert alert-danger mb-0 py-2';
        previewAmt.textContent = `₹${fine.toFixed(2)} (${lateDays} day${lateDays > 1 ? 's' : ''} late)`;
    }
}

function hideFinePreview() {
    document.getElementById('finePreview').classList.add('d-none');
}

// ─── Submit return ────────────────────────────────────────────────────────────
async function handleReturnSubmit(e) {
    e.preventDefault();

    const issueId    = parseInt(document.getElementById('issueSelect').value);
    const returnDate = document.getElementById('returnDate').value;

    if (!issueId) {
        showAlert('Please select an active issue to return.', 'warning');
        return;
    }

    const payload = { issueId };
    if (returnDate) payload.returnDate = returnDate;

    const btn = document.getElementById('returnBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Processing…';

    try {
        const res  = await fetch(RETURNS_API, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload)
        });
        const json = await res.json();

        if (!res.ok || !json.success) {
            throw new Error(json.message || 'Return failed');
        }

        const data = json.data;
        const fineMsg = parseFloat(data.fine) > 0
            ? `Fine applied: ₹${parseFloat(data.fine).toFixed(2)}`
            : 'No fine — returned on time!';

        showAlert(
            `✅ Book returned successfully! ${fineMsg}`,
            parseFloat(data.fine) > 0 ? 'warning' : 'success'
        );

        resetReturnForm();
        await loadActiveIssues();   // refresh dropdown (removes returned issue)
        await loadAllReturns();     // refresh table

    } catch (err) {
        showAlert('Error: ' + err.message, 'danger');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-arrow-return-left me-1"></i> Return Book';
    }
}

function resetReturnForm() {
    document.getElementById('returnForm').reset();
    setDefaultReturnDate();
    hideIssueDetails();
    hideFinePreview();
}

// ─── Load all return records ──────────────────────────────────────────────────
async function loadAllReturns() {
    try {
        const res  = await fetch(RETURNS_API);
        const json = await res.json();
        if (!json.success) throw new Error(json.message || 'Failed to load returns');
        renderReturnsTable(json.data || []);
    } catch (err) {
        console.error('loadAllReturns error:', err);
        document.getElementById('returnsTableBody').innerHTML =
            `<tr><td colspan="9" class="text-center text-danger py-3">Error loading records: ${escapeHtml(err.message)}</td></tr>`;
    }
}

// ─── Search ───────────────────────────────────────────────────────────────────
async function doSearch() {
    const keyword = document.getElementById('searchInput').value.trim();
    try {
        const url  = `${RETURNS_API}/search?keyword=${encodeURIComponent(keyword)}`;
        const res  = await fetch(url);
        const json = await res.json();
        if (!json.success) throw new Error(json.message || 'Search failed');
        renderReturnsTable(json.data || []);
    } catch (err) {
        console.error('doSearch error:', err);
    }
}

function clearSearch() {
    document.getElementById('searchInput').value = '';
    loadAllReturns();
}

// ─── Render returns table ─────────────────────────────────────────────────────
function renderReturnsTable(returns) {
    const tbody = document.getElementById('returnsTableBody');
    document.getElementById('returnCount').textContent = `${returns.length} record${returns.length !== 1 ? 's' : ''}`;

    if (returns.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" class="text-center text-muted py-4">
            <i class="bi bi-inbox me-2"></i>No return records found.</td></tr>`;
        return;
    }

    tbody.innerHTML = returns.map(r => {
        const fineAmt   = parseFloat(r.fine || 0);
        const fineBadge = fineAmt > 0
            ? `<span class="badge bg-danger">₹${fineAmt.toFixed(2)}</span>`
            : `<span class="badge bg-success">₹0.00</span>`;
        const statusBadge = r.lateReturn
            ? `<span class="badge bg-warning text-dark">Late Return</span>`
            : `<span class="badge bg-success">On Time</span>`;

        return `<tr>
            <td>${r.returnId}</td>
            <td>${r.issueId}</td>
            <td>${escapeHtml(r.bookTitle)}<br><small class="text-muted">${escapeHtml(r.bookIsbn)}</small></td>
            <td>${escapeHtml(r.studentName)}<br><small class="text-muted">${escapeHtml(r.studentDepartment || '')}</small></td>
            <td>${formatDate(r.issueDate)}</td>
            <td>${formatDate(r.dueDate)}</td>
            <td>${formatDate(r.returnDate)}</td>
            <td>${fineBadge}</td>
            <td>${statusBadge}</td>
        </tr>`;
    }).join('');
}

// ─── Utility ──────────────────────────────────────────────────────────────────
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
