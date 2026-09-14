// Shared browser helpers for SentinelSCM pages.
// Pages are server-rendered; JS only draws charts and wires small interactions.

/** Fetch JSON from the API with the session cookie and CSRF header. */
async function apiFetch(path, options = {}) {
    const csrf = document.querySelector('meta[name="_csrf"]');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
    const headers = { 'Accept': 'application/json', ...(options.headers || {}) };
    if (options.body) headers['Content-Type'] = 'application/json';
    if (csrf && csrfHeader) headers[csrfHeader.content] = csrf.content;
    const res = await fetch(path, { credentials: 'same-origin', ...options, headers });
    if (res.status === 401) { window.location.href = '/login'; return null; }
    if (!res.ok) {
        let message = res.statusText;
        try { message = (await res.json()).message || message; } catch (_) { /* not JSON */ }
        throw new Error(message);
    }
    return res.status === 204 ? null : res.json();
}

/** Colour for a risk score, matching the badge palette. */
function riskColor(score) {
    if (score > 0.7) return '#F43F5E';
    if (score > 0.4) return '#F59E0B';
    return '#10B981';
}

/** Keep a range input and its <output> in sync. */
function bindRange(inputId, outputId) {
    const input = document.getElementById(inputId);
    const output = document.getElementById(outputId);
    if (!input || !output) return;
    const sync = () => { output.value = Number(input.value).toFixed(2); };
    input.addEventListener('input', sync);
    sync();
}

/** Simple tab switcher: buttons with data-tab, panels with data-panel. */
function bindTabs(container) {
    const root = typeof container === 'string' ? document.getElementById(container) : container;
    if (!root) return;
    const tabs = root.querySelectorAll('[data-tab]');
    const panels = root.querySelectorAll('[data-panel]');
    tabs.forEach(tab => tab.addEventListener('click', () => {
        tabs.forEach(t => t.classList.toggle('active', t === tab));
        panels.forEach(p => { p.hidden = p.dataset.panel !== tab.dataset.tab; });
    }));
}

/** Copy demo credentials into the login form. */
function fillLogin(email, password) {
    const e = document.getElementById('username');
    const p = document.getElementById('password');
    if (e) e.value = email;
    if (p) p.value = password;
    if (p) p.focus();
}
