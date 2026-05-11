/**
 * Enterprise Loan Pages — Interactive Logic
 */

/* ============================================================
   Loan List Page
   ============================================================ */

async function loadLoanList(page = 1) {
  const tbody = document.getElementById('loan-table-body');
  const statusFilter = document.getElementById('filter-status')?.value || '';

  if (!tbody) return;

  tbody.innerHTML = renderSkeletonRows(5);

  try {
    const res = await loanApi.getList(page, 10, statusFilter);
    tbody.innerHTML = res.data.records.length
      ? res.data.records.map(renderLoanRow).join('')
      : `<tr><td colspan="7" class="empty-state">暂无贷款记录</td></tr>`;

    renderPagination('loan-pagination', res.data.total, res.data.page, res.data.size, loadLoanList);
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state" style="color:var(--color-danger)">加载失败：${e.message}</td></tr>`;
  }
}

function renderLoanRow(loan) {
  return `
    <tr>
      <td><span class="text-mono">#${loan.id}</span></td>
      <td><strong>¥${formatMoney(loan.loanAmount)}</strong></td>
      <td>${loan.loanTerm} 个月</td>
      <td>${formatPercent(loan.interestRate)}</td>
      <td>${loan.creditScore ? loan.creditScore.toFixed(1) + ' 分' : '—'}</td>
      <td>${statusBadge(loan.status)}</td>
      <td>
        <div style="display:flex;gap:8px;">
          <a href="/enterprise/loans/${loan.id}" class="btn btn-sm btn-outline">详情</a>
          ${loan.status === 'APPROVED' ? `<button class="btn btn-sm btn-primary" onclick="signLoan(${loan.id})">签约</button>` : ''}
        </div>
      </td>
    </tr>
  `;
}

/* ============================================================
   Loan Apply Page
   ============================================================ */

async function submitLoanApply(e) {
  e.preventDefault();

  const form = {
    loanAmount: parseFloat(document.getElementById('loanAmount').value),
    loanTerm: parseInt(document.getElementById('loanTerm').value),
    loanPurpose: document.getElementById('loanPurpose').value,
    repaymentMethod: document.getElementById('repaymentMethod').value,
  };

  const btn = document.getElementById('submit-btn');
  btn.disabled = true;
  btn.textContent = '提交中...';

  try {
    await loanApi.apply(form);
    showToast('贷款申请已提交！', 'success');
    setTimeout(() => { window.location.href = '/enterprise/loans'; }, 1500);
  } catch (e) {
    showToast(e.message, 'error');
    btn.disabled = false;
    btn.textContent = '提交申请';
  }
}

/* ============================================================
   Sign Loan
   ============================================================ */

async function signLoan(id) {
  if (!confirm('确认签订此贷款合同？签约后将进入放款流程。')) return;

  try {
    await loanApi.sign(id);
    showToast('签约成功！', 'success');
    loadLoanList();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

/* ============================================================
   Loan Detail Page
   ============================================================ */

async function loadLoanDetail() {
  const container = document.getElementById('loan-detail-content');
  if (!container) return;

  const id = container.dataset.loanId;
  if (!id) return;

  try {
    const res = await loanApi.getDetail(id);
    renderLoanDetail(res.data);
  } catch (e) {
    container.innerHTML = `<div class="glass-card" style="padding:var(--space-8);text-align:center;color:var(--color-danger);">加载失败：${e.message}</div>`;
  }
}

function renderLoanDetail(loan) {
  document.getElementById('detail-loan-id').textContent = '#' + loan.id;
  document.getElementById('detail-amount').textContent = '¥' + formatMoney(loan.loanAmount);
  document.getElementById('detail-term').textContent = loan.loanTerm + ' 个月';
  document.getElementById('detail-rate').textContent = formatPercent(loan.interestRate);
  document.getElementById('detail-purpose').textContent = loan.loanPurpose || '—';
  document.getElementById('detail-method').textContent = loan.repaymentMethod === 'EQUAL_INSTALLMENT' ? '等额本息' : '等额本金';
  document.getElementById('detail-date').textContent = formatDate(loan.applyDate);
  document.getElementById('detail-status').innerHTML = statusBadge(loan.status);

  if (loan.creditScore) {
    drawCreditGauge(loan.creditScore);
  }

  if (loan.approveComment) {
    document.getElementById('detail-comment').textContent = loan.approveComment;
  }
}

/* ============================================================
   Credit Score Gauge
   ============================================================ */

function drawCreditGauge(score) {
  const container = document.getElementById('credit-gauge');
  if (!container) return;

  const radius = 68;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;

  let color = 'var(--color-success)';
  let riskLevel = '低风险';
  let riskClass = 'risk-low';

  if (score < 40) {
    color = 'var(--color-danger)';
    riskLevel = '高风险';
    riskClass = 'risk-high';
  } else if (score < 70) {
    color = 'var(--color-warning)';
    riskLevel = '中风险';
    riskClass = 'risk-medium';
  }

  container.innerHTML = `
    <div class="credit-gauge-ring">
      <svg width="160" height="160" viewBox="0 0 160 160">
        <circle class="credit-gauge-bg" cx="80" cy="80" r="${radius}" stroke-width="8"/>
        <circle class="credit-gauge-fill"
          cx="80" cy="80" r="${radius}" stroke-width="8"
          stroke="${color}"
          stroke-dasharray="${circumference}"
          stroke-dashoffset="${offset}"
          stroke-linecap="round"/>
      </svg>
      <div class="credit-gauge-center">
        <div class="credit-gauge-score" style="color:${color}">${score.toFixed(0)}</div>
        <div class="credit-gauge-label">信用评分</div>
      </div>
    </div>
    <div class="credit-gauge-risk ${riskClass}">${riskLevel}</div>
  `;
}

/* ============================================================
   Repayments Page
   ============================================================ */

async function loadRepayments(loanId) {
  const tbody = document.getElementById('repayment-table-body');
  if (!tbody) return;

  tbody.innerHTML = renderSkeletonRows(3);

  try {
    const res = await loanApi.getRepayments(loanId);
    tbody.innerHTML = res.data.length
      ? res.data.map(r => `
        <tr>
          <td>第 ${r.periodNo} 期</td>
          <td>¥${formatMoney(r.amount)}</td>
          <td>¥${formatMoney(r.paidAmount)}</td>
          <td>${formatDate(r.dueDate)}</td>
          <td>${formatDate(r.paidDate)}</td>
          <td>${statusBadge(r.status)}</td>
          <td>
            ${r.status === 'UNPAID' ? `<button class="btn btn-sm btn-primary" onclick="payRepayment(${r.id})">还款</button>` : ''}
          </td>
        </tr>
      `).join('')
      : `<tr><td colspan="7" class="empty-state">暂无还款记录</td></tr>`;
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state" style="color:var(--color-danger)">加载失败：${e.message}</td></tr>`;
  }
}

async function payRepayment(id) {
  if (!confirm('确认支付本期还款？')) return;

  try {
    await loanApi.payRepayment(id);
    showToast('还款成功！', 'success');
    const loanId = document.getElementById('repayment-table-body')?.dataset.loanId;
    if (loanId) loadRepayments(loanId);
  } catch (e) {
    showToast(e.message, 'error');
  }
}

/* ============================================================
   Calculator Page
   ============================================================ */

function runCalculator() {
  const amount = parseFloat(document.getElementById('calc-amount').value);
  const term = parseInt(document.getElementById('calc-term').value);
  const rate = parseFloat(document.getElementById('calc-rate').value) || 0.0435;
  const mode = document.getElementById('calc-mode').value;

  if (!amount || !term) {
    showToast('请填写贷款金额和期限', 'warning');
    return;
  }

  const result = calculate(amount, term, rate, mode);
  document.getElementById('calc-monthly').textContent = '¥' + formatMoney(result.monthlyPayment);
  document.getElementById('calc-interest').textContent = '¥' + formatMoney(result.totalInterest);
  document.getElementById('calc-total').textContent = '¥' + formatMoney(result.totalPayment);

  const tbody = document.getElementById('calc-schedule-body');
  tbody.innerHTML = result.schedule.map(s => `
    <tr>
      <td>第 ${s.period} 期</td>
      <td>¥${formatMoney(s.principal)}</td>
      <td>¥${formatMoney(s.interest)}</td>
      <td>¥${formatMoney(s.payment)}</td>
      <td>¥${formatMoney(s.remaining)}</td>
    </tr>
  `).join('');
}

/* ============================================================
   Helpers
   ============================================================ */

function renderSkeletonRows(count) {
  return Array.from({ length: count }, () => `
    <tr>
      <td><div class="skeleton skeleton-text"></div></td>
      <td><div class="skeleton skeleton-text"></div></td>
      <td><div class="skeleton skeleton-text"></div></td>
      <td><div class="skeleton skeleton-text"></div></td>
      <td><div class="skeleton skeleton-text"></div></td>
      <td><div class="skeleton skeleton-text"></div></td>
      <td><div class="skeleton skeleton-text"></div></td>
    </tr>
  `).join('');
}

function renderPagination(containerId, total, currentPage, size, onPageChange) {
  const container = document.getElementById(containerId);
  if (!container) return;

  const totalPages = Math.ceil(total / size);
  if (totalPages <= 1) { container.innerHTML = ''; return; }

  let html = `<button class="pagination-btn" ${currentPage <= 1 ? 'disabled' : ''} onclick="event.preventDefault();(${onPageChange.name})(${currentPage - 1})">上一页</button>`;

  for (let i = 1; i <= totalPages; i++) {
    if (i === 1 || i === totalPages || (i >= currentPage - 1 && i <= currentPage + 1)) {
      html += `<button class="pagination-btn ${i === currentPage ? 'active' : ''}" onclick="event.preventDefault();(${onPageChange.name})(${i})">${i}</button>`;
    } else if (i === currentPage - 2 || i === currentPage + 2) {
      html += `<span class="pagination-ellipsis">...</span>`;
    }
  }

  html += `<button class="pagination-btn" ${currentPage >= totalPages ? 'disabled' : ''} onclick="event.preventDefault();(${onPageChange.name})(${currentPage + 1})">下一页</button>`;
  html += `<span style="font-size:var(--text-xs);color:var(--color-text-muted);margin-left:var(--space-2)">共 ${total} 条</span>`;
  container.innerHTML = html;
}

/* ============================================================
   Logout
   ============================================================ */

function logout() {
  if (confirm('确认退出登录？')) {
    clearToken();
    window.location.href = '/enterprise/login';
  }
}

/* ============================================================
   Mobile Sidebar Toggle
   ============================================================ */

function initSidebar() {
  const toggle = document.getElementById('sidebar-toggle');
  const sidebar = document.getElementById('enterprise-sidebar');
  const overlay = document.getElementById('sidebar-overlay');

  if (!toggle || !sidebar || !overlay) return;

  toggle.addEventListener('click', () => {
    sidebar.classList.toggle('open');
    overlay.classList.toggle('open');
  });

  overlay.addEventListener('click', () => {
    sidebar.classList.remove('open');
    overlay.classList.remove('open');
  });
}

document.addEventListener('DOMContentLoaded', () => {
  initSidebar();
});
