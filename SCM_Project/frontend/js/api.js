// api.js — Shared API client for all pages
// OOAD: Indirection pattern - centralizes all API calls

// Always point to the Java backend on 8080
const API_BASE = 'http://localhost:8080/api';

// ─── Role-based page access map ──────────────────────────────────────────────
// Defines which pages (by initPage key) each role is allowed to visit
const ROLE_PERMISSIONS = {
    'ADMINISTRATOR':        ['dashboard','vendors','risk','alerts','inventory','reports'],
    'PROCUREMENT_MANAGER':  ['dashboard','vendors','alerts','reports'],
    'RISK_ANALYST':         ['dashboard','vendors','risk','alerts','reports'],
    'WAREHOUSE_MANAGER':    ['dashboard','inventory'],
    'VENDOR':               ['dashboard','vendors'],
};

/** Returns true if the current user's role can access the given page key */
function hasPageAccess(pageKey) {
    const user = getCurrentUser();
    if (!user) return false;
    const allowed = ROLE_PERMISSIONS[user.role] || [];
    return allowed.includes(pageKey);
}

// Auth guard: redirect to login if no token (for protected pages)
(function() {
    if (!window.location.pathname.endsWith('login.html')) {
        if (!localStorage.getItem('token')) {
            window.location.href = '/login.html';
        }
    }
})();

/** Core fetch wrapper with Bearer token auth header */
async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem('token');
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    try {
        const res = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
        if (res.status === 401) {
            localStorage.removeItem('token'); localStorage.removeItem('user');
            window.location.href = '/login.html';
            return null;
        }
        // 403 = role lacks permission — return null gracefully, page handles it
        if (res.status === 403) return null;
        if (!res.ok) return null;
        return await res.json();
    } catch (err) {
        console.error(`[API Error] ${endpoint}:`, err);
        return null;
    }
}

/** Shared API helpers */
const API = {
    getVendors:          () => apiFetch('/vendor'),
    getVendorById:       (id) => apiFetch(`/vendor/${id}`),
    createVendor:        (data) => apiFetch('/vendor', { method: 'POST', body: JSON.stringify(data) }),
    updateVendor:        (id, data) => apiFetch(`/vendor/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    deleteVendor:        (id) => apiFetch(`/vendor/${id}`, { method: 'DELETE' }),
    suspendVendor:       (id) => apiFetch(`/vendor/${id}/suspend`, { method: 'PUT' }),
    activateVendor:      (id) => apiFetch(`/vendor/${id}/activate`, { method: 'PUT' }),
    blacklistVendor:     (id) => apiFetch(`/vendor/${id}/blacklist`, { method: 'PUT' }),
    evaluateVendor:      (id, data) => apiFetch(`/vendor/${id}/evaluate`, { method: 'POST', body: JSON.stringify(data) }),
    calculateRisk:       (id) => apiFetch(`/risk/calculate/${id}`, { method: 'POST' }),
    getVendorRisk:       (id) => apiFetch(`/vendor/${id}/risk`),
    getAlerts:           () => apiFetch('/alert'),
    resolveAlert:        (id) => apiFetch(`/alert/${id}/resolve`, { method: 'PUT' }),
    getRecommendations:  (id) => apiFetch(`/alert/${id}/recommendation`),
    getInventory:        () => apiFetch('/inventory'),
    updateStock:         (id, quantity) => apiFetch(`/inventory/${id}`, { method: 'PUT', body: JSON.stringify({ quantity }) }),
    getPerformanceReport:() => apiFetch('/report/performance'),
    getStrategicReport:  () => apiFetch('/report/strategic'),
};

/** Get current user from localStorage */
function getCurrentUser() {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
}

/** Logout function */
function logout() {
    const token = localStorage.getItem('token');
    if (token) fetch(`${API_BASE}/auth/logout`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ token }) });
    localStorage.removeItem('token'); localStorage.removeItem('user');
    window.location.href = '/login.html';
}

/** Build standard sidebar nav HTML */
function buildSidebar(activePage) {
    const user = getCurrentUser();
    const name  = user?.name  || 'User';
    const role  = user?.role  || '';
    const init  = name.charAt(0).toUpperCase();
    const roleBadge = role.replace(/_/g, ' ');

    const navItems = [
        { href: '/dashboard.html', icon: 'dashboard',       label: 'Dashboard',  key: 'dashboard' },
        { href: '/vendors.html',   icon: 'corporate_fare',  label: 'Vendors',    key: 'vendors' },
        { href: '/risk.html',      icon: 'analytics',       label: 'Risk',       key: 'risk' },
        { href: '/alerts.html',    icon: 'warning',         label: 'Alerts',     key: 'alerts' },
        { href: '/inventory.html', icon: 'inventory_2',     label: 'Inventory',  key: 'inventory' },
        { href: '/reports.html',   icon: 'bar_chart',       label: 'Reports',    key: 'reports' },
    ];

    const allowed = ROLE_PERMISSIONS[role] || [];
    const navHTML = navItems.map(item => {
        const active  = activePage === item.key;
        const canView = allowed.includes(item.key);
        if (!canView) {
            return `<span class="nav-item nav-item-locked" title="Access restricted for your role">
                <span class="material-symbols-outlined">${item.icon}</span>
                <span>${item.label}</span>
                <span class="material-symbols-outlined" style="font-size:14px;margin-left:auto;opacity:0.5">lock</span>
            </span>`;
        }
        return `<a href="${item.href}" class="nav-item ${active ? 'active' : ''}">
            <span class="material-symbols-outlined">${item.icon}</span>
            <span>${item.label}</span>
        </a>`;
    }).join('');

    return `<nav id="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo"><span class="material-symbols-outlined">hub</span></div>
            <div><div class="brand-name">SupplyChainOS</div><div class="brand-status"><span class="pulse-dot"></span>Live</div></div>
        </div>
        <div class="sidebar-user">
            <div class="user-avatar">${init}</div>
            <div><div class="user-name">${name}</div><div class="user-role">${roleBadge}</div></div>
        </div>
        <div class="nav-items">${navHTML}</div>
        <div class="sidebar-footer">
            <button onclick="logout()" class="nav-item logout-item">
                <span class="material-symbols-outlined">logout</span><span>Sign Out</span>
            </button>
        </div>
    </nav>`;
}

/** Shared page CSS variables injected into <head> */
const SHARED_CSS = `
<style>
:root {
    --bg: #0B1120; --surface: #111B2E; --surface2: #162035; --surface3: #1C2A45;
    --primary: #06B6D4; --primary-dim: rgba(6,182,212,0.15); --primary-glow: rgba(6,182,212,0.35);
    --success: #10B981; --error: #F43F5E; --warning: #F59E0B;
    --text: #E2E8F0; --text-muted: #64748B; --text-dim: #8892A4;
    --border: rgba(255,255,255,0.07); --sidebar-w: 240px;
}
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
html, body { height: 100%; }
body { font-family: 'Inter', sans-serif; background: var(--bg); color: var(--text); display: flex; min-height: 100vh; overflow: hidden; }
a { text-decoration: none; }

/* SIDEBAR */
#sidebar { width: var(--sidebar-w); flex-shrink: 0; background: var(--surface); border-right: 1px solid var(--border); display: flex; flex-direction: column; padding: 1.25rem 0; position: fixed; top: 0; left: 0; height: 100vh; z-index: 100; }
.sidebar-brand { display: flex; align-items: center; gap: 10px; padding: 0 1.25rem 1.25rem; border-bottom: 1px solid var(--border); }
.brand-logo { width: 36px; height: 36px; background: linear-gradient(135deg, var(--primary), #0891B2); border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.brand-logo .material-symbols-outlined { font-size: 20px; color: #000; }
.brand-name { font-weight: 800; font-size: 0.9rem; letter-spacing: -0.3px; }
.brand-status { display: flex; align-items: center; gap: 5px; font-size: 0.65rem; color: var(--success); text-transform: uppercase; letter-spacing: 0.08em; }
.pulse-dot { width: 6px; height: 6px; background: var(--success); border-radius: 50%; animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{opacity:1;transform:scale(1)} 50%{opacity:.5;transform:scale(1.3)} }
.sidebar-user { display: flex; align-items: center; gap: 10px; padding: 1rem 1.25rem; }
.user-avatar { width: 34px; height: 34px; border-radius: 50%; background: linear-gradient(135deg,var(--primary),#0891B2); display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.85rem; color: #000; flex-shrink: 0; }
.user-name { font-size: 0.82rem; font-weight: 600; }
.user-role { font-size: 0.68rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; }
.nav-items { flex: 1; padding: 0.5rem 0.75rem; display: flex; flex-direction: column; gap: 2px; overflow-y: auto; }
.nav-item { display: flex; align-items: center; gap: 10px; padding: 9px 12px; border-radius: 8px; color: var(--text-muted); font-size: 0.83rem; font-weight: 500; transition: all 0.15s; cursor: pointer; border: none; background: none; width: 100%; }
.nav-item:hover { background: rgba(255,255,255,0.05); color: var(--text); }
.nav-item.active { background: var(--primary-dim); color: var(--primary); }
.nav-item .material-symbols-outlined { font-size: 20px; }
.nav-item-locked { opacity: 0.35; cursor: not-allowed !important; pointer-events: none; }
.sidebar-footer { padding: 0.75rem; border-top: 1px solid var(--border); }
.logout-item { color: var(--text-muted) !important; }
.logout-item:hover { background: rgba(244,63,94,0.1) !important; color: var(--error) !important; }

/* MAIN CONTENT */
#main-content { margin-left: var(--sidebar-w); flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.page-header { padding: 1.75rem 2rem 0; }
.page-title { font-size: 1.6rem; font-weight: 800; letter-spacing: -0.5px; }
.page-subtitle { color: var(--text-muted); font-size: 0.85rem; margin-top: 4px; }
.page-body { padding: 1.5rem 2rem 2rem; overflow-y: auto; flex: 1; }

/* CARDS */
.card { background: var(--surface); border: 1px solid var(--border); border-radius: 14px; padding: 1.5rem; }
.card-title { font-size: 0.72rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.12em; margin-bottom: 1rem; }

/* KPI CARDS */
.kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: 1rem; margin-bottom: 1.5rem; }
.kpi-card { background: var(--surface); border: 1px solid var(--border); border-radius: 14px; padding: 1.25rem; }
.kpi-label { font-size: 0.68rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.1em; margin-bottom: 0.75rem; }
.kpi-value { font-size: 1.75rem; font-weight: 800; letter-spacing: -1px; }
.kpi-value.danger { color: var(--error); }
.kpi-value.success { color: var(--success); }
.kpi-value.primary { color: var(--primary); }

/* BADGES */
.badge { display: inline-flex; align-items: center; gap: 4px; padding: 3px 10px; border-radius: 20px; font-size: 0.7rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; }
.badge-high    { background: rgba(244,63,94,0.15);  color: var(--error);  border: 1px solid rgba(244,63,94,0.3); }
.badge-medium  { background: rgba(245,158,11,0.15); color: var(--warning);border: 1px solid rgba(245,158,11,0.3); }
.badge-low     { background: rgba(16,185,129,0.15); color: var(--success);border: 1px solid rgba(16,185,129,0.3); }
.badge-active  { background: rgba(16,185,129,0.15); color: var(--success);border: 1px solid rgba(16,185,129,0.3); }
.badge-pending { background: rgba(6,182,212,0.12);  color: var(--primary);border: 1px solid rgba(6,182,212,0.25); }
.badge-suspended{background: rgba(245,158,11,0.15); color: var(--warning);border: 1px solid rgba(245,158,11,0.3); }
.badge-blacklisted,.badge-inactive{background:rgba(100,116,139,0.15);color:var(--text-muted);border:1px solid rgba(100,116,139,0.3);}
.badge-high-risk { background: rgba(244,63,94,0.15); color: var(--error); border: 1px solid rgba(244,63,94,0.3); }

/* TABLES */
.table-wrap { overflow-x: auto; border-radius: 12px; border: 1px solid var(--border); }
table { width: 100%; border-collapse: collapse; }
thead tr { border-bottom: 1px solid var(--border); }
th { padding: 10px 16px; text-align: left; font-size: 0.68rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.1em; background: var(--surface2); white-space: nowrap; }
td { padding: 13px 16px; font-size: 0.85rem; border-bottom: 1px solid rgba(255,255,255,0.04); }
tr:last-child td { border-bottom: none; }
tr:hover td { background: rgba(255,255,255,0.02); }

/* BUTTONS */
.btn { display: inline-flex; align-items: center; gap: 6px; padding: 8px 16px; border-radius: 8px; font-size: 0.82rem; font-weight: 600; cursor: pointer; border: none; transition: all 0.2s; font-family: inherit; }
.btn-primary { background: var(--primary); color: #000; }
.btn-primary:hover { box-shadow: 0 4px 15px var(--primary-glow); transform: translateY(-1px); }
.btn-danger  { background: rgba(244,63,94,0.15); color: var(--error); border: 1px solid rgba(244,63,94,0.3); }
.btn-danger:hover  { background: rgba(244,63,94,0.25); }
.btn-ghost   { background: rgba(255,255,255,0.05); color: var(--text-muted); border: 1px solid var(--border); }
.btn-ghost:hover { background: rgba(255,255,255,0.09); color: var(--text); }
.btn-sm { padding: 5px 12px; font-size: 0.75rem; }

/* MODAL */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(4px); z-index: 200; display: none; align-items: center; justify-content: center; }
.modal-overlay.open { display: flex; }
.modal { background: var(--surface); border: 1px solid var(--border); border-radius: 18px; padding: 2rem; width: 100%; max-width: 480px; margin: 1rem; max-height: 90vh; overflow-y: auto; box-shadow: 0 25px 60px rgba(0,0,0,0.5); }
.modal-title { font-size: 1.1rem; font-weight: 700; margin-bottom: 1.5rem; }
.form-field { margin-bottom: 1.1rem; }
.form-field label { display: block; font-size: 0.75rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.08em; margin-bottom: 6px; }
.form-field input, .form-field select, .form-field textarea {
    width: 100%; background: rgba(255,255,255,0.05); border: 1px solid var(--border);
    border-radius: 8px; padding: 10px 14px; color: var(--text); font-size: 0.875rem;
    font-family: inherit; outline: none; transition: all 0.2s;
}
.form-field input:focus, .form-field select:focus { border-color: var(--primary); box-shadow: 0 0 0 3px rgba(6,182,212,0.12); }
.form-field select option { background: var(--surface2); }
.modal-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 1.5rem; }

/* TOAST */
#toast { position: fixed; bottom: 1.5rem; right: 1.5rem; z-index: 9999; display: flex; flex-direction: column; gap: 8px; }
.toast-item { background: var(--surface3); border: 1px solid var(--border); border-radius: 10px; padding: 12px 18px; font-size: 0.83rem; display: flex; align-items: center; gap: 8px; animation: slideIn 0.3s ease; box-shadow: 0 8px 25px rgba(0,0,0,0.4); }
.toast-item.success { border-color: rgba(16,185,129,0.4); }
.toast-item.error   { border-color: rgba(244,63,94,0.4); }
@keyframes slideIn { from{opacity:0;transform:translateX(20px)} to{opacity:1;transform:translateX(0)} }

/* UTILS */
.flex { display: flex; } .flex-between { display: flex; justify-content: space-between; align-items: center; }
.gap-2 { gap: 0.5rem; } .gap-3 { gap: 0.75rem; } .mb-4 { margin-bottom: 1rem; } .mb-6 { margin-bottom: 1.5rem; }
.grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; }
.grid-3 { display: grid; grid-template-columns: repeat(3,1fr); gap: 1.25rem; }
.text-center { text-align: center; } .text-muted { color: var(--text-muted); font-size: 0.85rem; }
.loading { text-align: center; padding: 3rem; color: var(--text-muted); }
</style>
`;

/** Inject sidebar + shared styles into page */
function initPage(activePage) {
    // Inject CSS
    document.head.insertAdjacentHTML('beforeend', SHARED_CSS);
    // Inject sidebar before #main-content
    const main = document.getElementById('main-content');
    if (main) main.insertAdjacentHTML('beforebegin', buildSidebar(activePage));
    // Setup logout buttons
    document.querySelectorAll('.logout-item').forEach(el => el.addEventListener('click', logout));

    // ── Role-based access guard ──────────────────────────────────────────────
    // dashboard is always allowed (data is scoped inside the page per role)
    if (activePage !== 'dashboard' && !hasPageAccess(activePage)) {
        const user = getCurrentUser();
        const role = (user?.role || 'UNKNOWN').replace(/_/g, ' ');
        const pageLabel = activePage.charAt(0).toUpperCase() + activePage.slice(1);
        if (main) main.innerHTML = `
            <div style="display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;gap:1.5rem;text-align:center;padding:2rem">
                <div style="width:80px;height:80px;border-radius:50%;background:rgba(244,63,94,0.12);border:2px solid rgba(244,63,94,0.3);display:flex;align-items:center;justify-content:center">
                    <span class="material-symbols-outlined" style="font-size:38px;color:var(--error)">lock</span>
                </div>
                <div>
                    <h2 style="font-size:1.5rem;font-weight:800;color:var(--error);margin-bottom:0.5rem">Access Not Granted</h2>
                    <p style="color:var(--text-muted);font-size:0.9rem;max-width:380px">
                        Your role (<strong style="color:var(--text)">${role}</strong>) does not have permission
                        to access the <strong style="color:var(--text)">${pageLabel}</strong> page.
                    </p>
                    <p style="color:var(--text-muted);font-size:0.8rem;margin-top:0.5rem">Contact your administrator to request access.</p>
                </div>
                <a href="/dashboard.html" style="display:inline-flex;align-items:center;gap:8px;padding:10px 22px;border-radius:8px;background:var(--primary);color:#000;font-weight:700;font-size:0.85rem;text-decoration:none">
                    <span class="material-symbols-outlined" style="font-size:18px">home</span> Go to Dashboard
                </a>
            </div>`;
    }
}

/** Show toast notification */
function showToast(message, type = 'success') {
    let container = document.getElementById('toast');
    if (!container) { container = document.createElement('div'); container.id = 'toast'; document.body.appendChild(container); }
    const icon = type === 'success' ? 'check_circle' : type === 'error' ? 'error' : 'info';
    const color = type === 'success' ? 'var(--success)' : type === 'error' ? 'var(--error)' : 'var(--primary)';
    const item = document.createElement('div');
    item.className = `toast-item ${type}`;
    item.innerHTML = `<span class="material-symbols-outlined" style="color:${color};font-size:18px">${icon}</span>${message}`;
    container.appendChild(item);
    setTimeout(() => item.remove(), 3500);
}

/** Get risk badge HTML */
function riskBadge(score) {
    const s = parseFloat(score);
    if (s > 0.7) return `<span class="badge badge-high">⬤ HIGH</span>`;
    if (s > 0.4) return `<span class="badge badge-medium">⬤ MEDIUM</span>`;
    return `<span class="badge badge-low">⬤ LOW</span>`;
}

/** Get status badge HTML — explicit map handles HIGH_RISK, BLACKLISTED etc */
function statusBadge(status) {
    const s = (status || '').toUpperCase();
    const classMap = {
        'ACTIVE':      'active',
        'PENDING':     'pending',
        'SUSPENDED':   'suspended',
        'HIGH_RISK':   'high-risk',
        'BLACKLISTED': 'blacklisted',
        'INACTIVE':    'inactive',
    };
    const cls   = classMap[s] || 'pending';
    const label = s === 'HIGH_RISK' ? 'HIGH RISK' : (status || 'UNKNOWN');
    return `<span class="badge badge-${cls}">${label}</span>`;
}
