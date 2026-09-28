/**
 * Books Module - College Library Management System
 * Full CRUD, multi-field search, stock availability tracking, and validation.
 */

let deleteModalInstance = null;
let currentDeleteId = null;

document.addEventListener('DOMContentLoaded', () => {
    const modalEl = document.getElementById('deleteModal');
    if (modalEl) {
        deleteModalInstance = new bootstrap.Modal(modalEl);
    }
    loadBooks();
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
 * Load and render all books
 */
async function loadBooks() {
    const tbody = document.getElementById('booksTableBody');
    const countBadge = document.getElementById('bookCountBadge');

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/books`);
        if (!response.ok) {
            throw new Error(`Failed to load books (Status: ${response.status})`);
        }
        const books = await response.json();
        renderBooksTable(books);
    } catch (error) {
        console.error('Error loading books:', error);
        tbody.innerHTML = `
            <tr>
                <td colspan="10" class="text-center py-4 text-danger">
                    <i class="bi bi-exclamation-octagon fs-4 d-block mb-2"></i>
                    Unable to connect to the backend server at <code>${CONFIG.API_BASE_URL}/books</code>.<br>
                    <small class="text-muted">Ensure Spring Boot is running and database is accessible.</small>
                </td>
            </tr>
        `;
        if (countBadge) countBadge.textContent = '0 Books';
    }
}

/**
 * Render table rows
 */
function renderBooksTable(books) {
    const tbody = document.getElementById('booksTableBody');
    const countBadge = document.getElementById('bookCountBadge');

    if (countBadge) {
        countBadge.textContent = `${books.length} Book${books.length === 1 ? '' : 's'}`;
    }

    if (!books || books.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="10" class="text-center py-4 text-muted">
                    <i class="bi bi-inbox fs-3 d-block mb-1"></i>
                    No books found.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = books.map(book => {
        const isAvailable = Number(book.availableCopies) > 0;
        const statusBadge = isAvailable
            ? '<span class="badge bg-success">Available</span>'
            : '<span class="badge bg-danger">Not Available</span>';

        return `
            <tr>
                <td class="fw-semibold text-muted">#${book.bookId}</td>
                <td class="fw-bold">${escapeHtml(book.title)}</td>
                <td>${escapeHtml(book.author)}</td>
                <td>${escapeHtml(book.publisher || '-')}</td>
                <td><span class="badge bg-light text-dark border">${escapeHtml(book.category || 'General')}</span></td>
                <td><code>${escapeHtml(book.isbn)}</code></td>
                <td class="text-center">${book.totalCopies}</td>
                <td class="text-center fw-semibold ${isAvailable ? 'text-success' : 'text-danger'}">${book.availableCopies}</td>
                <td class="text-center">${statusBadge}</td>
                <td class="text-center">
                    <button class="btn btn-sm btn-outline-primary me-1" title="Edit" onclick="editBook(${book.bookId})">
                        <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" title="Delete" onclick="openDeleteModal(${book.bookId}, '${escapeHtml(book.title).replace(/'/g, "\\'")}')">
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
        loadBooks();
        return;
    }

    const tbody = document.getElementById('booksTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="10" class="text-center py-4 text-muted">
                <div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>
                Searching books for "${escapeHtml(keyword)}"...
            </td>
        </tr>
    `;

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/books/search?keyword=${encodeURIComponent(keyword)}`);
        if (!response.ok) {
            throw new Error('Search failed');
        }
        const results = await response.json();
        renderBooksTable(results);
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
    loadBooks();
}

/**
 * Handle Form Submit (Add / Update)
 */
async function handleBookSubmit(event) {
    event.preventDefault();

    const bookId = document.getElementById('bookId').value;
    const title = document.getElementById('bookTitle').value.trim();
    const author = document.getElementById('bookAuthor').value.trim();
    const publisher = document.getElementById('bookPublisher').value.trim();
    const category = document.getElementById('bookCategory').value.trim();
    const isbn = document.getElementById('bookIsbn').value.trim();
    const totalCopies = parseInt(document.getElementById('totalCopies').value, 10);
    const availableInput = document.getElementById('availableCopies').value;
    const availableCopies = availableInput !== '' ? parseInt(availableInput, 10) : totalCopies;

    // Client-side quick validations
    if (totalCopies < 0) {
        showAlert('Total copies cannot be negative.', 'danger');
        return;
    }
    if (availableCopies < 0) {
        showAlert('Available copies cannot be negative.', 'danger');
        return;
    }
    if (availableCopies > totalCopies) {
        showAlert('Available copies cannot exceed total copies.', 'danger');
        return;
    }

    const payload = {
        title,
        author,
        publisher: publisher || null,
        category: category || null,
        isbn,
        totalCopies,
        availableCopies
    };

    const isEdit = Boolean(bookId);
    const url = isEdit ? `${CONFIG.API_BASE_URL}/books/${bookId}` : `${CONFIG.API_BASE_URL}/books`;
    const method = isEdit ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            const errorMsg = data.message || (isEdit ? 'Failed to update book.' : 'Failed to add book.');
            showAlert(errorMsg, 'danger');
            return;
        }

        showAlert(isEdit ? `Book "${title}" updated successfully.` : `Book "${title}" added successfully.`, 'success');
        resetForm();
        loadBooks();
    } catch (error) {
        console.error('Error saving book:', error);
        showAlert('Network error: Could not connect to the server.', 'danger');
    }
}

/**
 * Edit Book - Load details into form
 */
async function editBook(id) {
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/books/${id}`);
        if (!response.ok) {
            throw new Error('Could not fetch book details.');
        }
        const book = await response.json();

        document.getElementById('bookId').value = book.bookId;
        document.getElementById('bookTitle').value = book.title;
        document.getElementById('bookAuthor').value = book.author;
        document.getElementById('bookPublisher').value = book.publisher || '';
        document.getElementById('bookCategory').value = book.category || '';
        document.getElementById('bookIsbn').value = book.isbn;
        document.getElementById('totalCopies').value = book.totalCopies;
        document.getElementById('availableCopies').value = book.availableCopies;

        // Update UI state to Edit Mode
        document.getElementById('formTitle').innerHTML = '<i class="bi bi-pencil-square me-2 text-warning"></i>Edit Book';
        document.getElementById('editBadge').classList.remove('d-none');
        document.getElementById('saveBtn').innerHTML = '<i class="bi bi-arrow-repeat me-1"></i> Update Book';

        window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (error) {
        console.error('Error loading book for edit:', error);
        showAlert(error.message, 'danger');
    }
}

/**
 * Reset form back to Add mode
 */
function resetForm() {
    document.getElementById('bookForm').reset();
    document.getElementById('bookId').value = '';
    document.getElementById('formTitle').innerHTML = '<i class="bi bi-plus-circle me-2 text-primary"></i>Add New Book';
    document.getElementById('editBadge').classList.add('d-none');
    document.getElementById('saveBtn').innerHTML = '<i class="bi bi-check-lg me-1"></i> Add Book';
}

/**
 * Delete confirmation & execution
 */
function openDeleteModal(id, title) {
    currentDeleteId = id;
    document.getElementById('deleteBookTitle').textContent = `"${title}"`;
    if (deleteModalInstance) {
        deleteModalInstance.show();
    }
}

async function executeDelete() {
    if (!currentDeleteId) return;

    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/books/${currentDeleteId}`, {
            method: 'DELETE'
        });

        const data = await response.json();

        if (deleteModalInstance) {
            deleteModalInstance.hide();
        }

        if (!response.ok) {
            showAlert(data.message || 'Failed to delete book.', 'danger');
            return;
        }

        showAlert('Book deleted successfully.', 'success');
        loadBooks();
    } catch (error) {
        console.error('Error deleting book:', error);
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
