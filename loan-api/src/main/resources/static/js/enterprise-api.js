/**
 * Inclusive Finance Platform — API Client (Enterprise)
 * Handles JWT auth token, fetch wrapper, and enterprise API calls.
 */

const API_BASE = '/api';

/* ============================================================
   Token Management
   ============================================================ */

const tokenKey = 'ifp_enterprise_token';
const userKey = 'ifp_enterprise_user';

function getToken() {
  return localStorage.getItem(tokenKey);
}

function setToken(token) {
  localStorage.setItem(tokenKey, token);
}

function clearToken() {
  localStorage.removeItem(tokenKey);
  localStorage.removeItem(userKey);
}

function getUser() {
  try {
    return JSON.parse(localStorage.getItem(userKey));
  } catch {
    return null;
  }
}

function setUser(user) {
  localStorage.setItem(userKey, JSON.stringify(user));
}

function isAuthenticated() {
  return !!getToken();
}

/* ============================================================
   HTTP Client
   ============================================================ */

async function request(path, options = {}) {
  const { method = 'GET', body, params, noAuth = false } = options;

  let url = `${API_BASE}${path}`;
  if (params) {
    const qs = new URLSearchParams(params).toString();
    url += `?${qs}`;
  }

  const headers = { 'Content-Type': 'application/json' };

  if (!noAuth) {
    const token = getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
  }

  const config = { method, headers };
  if (body) {
    config.body = JSON.stringify(body);
  }

  const res = await fetch(url, config);

  if (res.status === 401) {
    clearToken();
    window.location.href = '/enterprise/login';
    throw new Error('认证已过期，请重新登录');
  }

  const data = await res.json();

  if (data.code !== 200) {
    throw new Error(data.message || '请求失败');
  }

  return data;
}

/* ============================================================
   Auth API
   ============================================================ */

const authApi = {
  login(username, password) {
    return request('/auth/login', {
      method: 'POST',
      body: { username, password },
      noAuth: true,
    });
  },

  register(form) {
    return request('/auth/register', {
      method: 'POST',
      body: form,
      noAuth: true,
    });
  },
};

/* ============================================================
   Enterprise Loan API
   ============================================================ */

const loanApi = {
  getList(page = 1, size = 10, status = '') {
    return request('/enterprise/loans', {
      params: { page, size, ...(status && { status }) },
    });
  },

  getDetail(id) {
    return request(`/enterprise/loans/${id}`);
  },

  apply(form) {
    return request('/enterprise/loans', {
      method: 'POST',
      body: form,
    });
  },

  sign(id) {
    return request(`/enterprise/loans/${id}/sign`, {
      method: 'POST',
    });
  },

  getRepayments(loanId, status = '') {
    return request('/enterprise/repayments', {
      params: { loanId, ...(status && { status }) },
    });
  },

  payRepayment(id) {
    return request(`/enterprise/repayments/${id}/pay`, {
      method: 'POST',
    });
  },

  getOverdue() {
    return request('/enterprise/overdue');
  },

  calculator(amount, term, rate, mode) {
    return request('/enterprise/calculator', {
      params: { amount, term, rate: rate || 0.0435, mode: mode || 'EQUAL_INSTALLMENT' },
    });
  },
};

/* ============================================================
   Toast Notification
   ============================================================ */

function showToast(message, type = 'info', duration = 3000) {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 300ms ease';
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

/* ============================================================
   Format Helpers
   ============================================================ */

function formatMoney(val) {
  if (val == null) return '—';
  return Number(val).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

function formatDate(val) {
  if (!val) return '—';
  const d = new Date(val);
  return d.toLocaleDateString('zh-CN');
}

function formatPercent(val) {
  if (val == null) return '—';
  return (Number(val) * 100).toFixed(2) + '%';
}

function statusBadge(status) {
  const map = {
    PENDING:  '<span class="badge badge-pending">待审批</span>',
    APPROVED: '<span class="badge badge-approved">已通过</span>',
    REJECTED: '<span class="badge badge-rejected">已拒绝</span>',
    GRANTED:  '<span class="badge badge-granted">已放款</span>',
    REPAID:   '<span class="badge badge-repaid">已还清</span>',
    OVERDUE:  '<span class="badge badge-overdue">已逾期</span>',
    PAID:     '<span class="badge badge-repaid">已还款</span>',
    UNPAID:   '<span class="badge badge-pending">待还款</span>',
  };
  return map[status] || `<span class="badge badge-pending">${status}</span>`;
}
