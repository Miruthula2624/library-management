/**
 * College Library Management System — Common JavaScript
 * Shared configuration, navigation helpers, and sidebar toggle.
 */

const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api'
};

document.addEventListener('DOMContentLoaded', () => {
    // ── Active nav link detection ────────────────────────────────
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    const menuLinks = document.querySelectorAll('.sidebar-menu a');

    menuLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPage || (currentPage === 'index.html' && href === 'dashboard.html')) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

    // ── Mobile sidebar toggle ────────────────────────────────────
    // Inject toggle button into top-navbar if viewport is narrow
    const sidebar  = document.querySelector('.sidebar');
    const navbar   = document.querySelector('.top-navbar');

    if (sidebar && navbar) {
        // Ensure toggle button exists
        let toggle = document.getElementById('sidebarToggle');
        if (!toggle) {
            toggle = document.createElement('button');
            toggle.id = 'sidebarToggle';
            toggle.setAttribute('aria-label', 'Toggle navigation');
            toggle.innerHTML = '<i class="bi bi-list"></i>';
            navbar.insertBefore(toggle, navbar.firstChild);
        }

        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });

        // Close sidebar when clicking a nav link on mobile
        menuLinks.forEach(link => {
            link.addEventListener('click', () => {
                if (window.innerWidth < 992) {
                    sidebar.classList.remove('open');
                }
            });
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', (e) => {
            if (window.innerWidth < 992 &&
                !sidebar.contains(e.target) &&
                !toggle.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        });
    }
});
