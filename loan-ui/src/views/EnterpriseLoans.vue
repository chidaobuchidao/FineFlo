<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">贷款列表</h2>
        <p class="page-desc">查看和管理您的贷款申请</p>
      </div>
      <div class="page-actions">
        <select v-model="statusFilter" class="form-select" @change="fetchData(1)">
          <option value="">全部状态</option>
          <option value="PENDING">待审批</option>
          <option value="APPROVED">已通过</option>
          <option value="GRANTED">已放款</option>
          <option value="REPAID">已还清</option>
          <option value="OVERDUE">已逾期</option>
        </select>
        <router-link to="/loans/apply" class="btn btn-primary">+ 申请贷款</router-link>
      </div>
    </div>

    <!-- Stats -->
    <div class="stats-row">
      <div class="stat-card glass-card">
        <div class="stat-label">贷款笔数</div>
        <div class="stat-value">{{ stats.total }}</div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-label">待审批</div>
        <div class="stat-value" style="color: var(--color-warning)">{{ stats.pending }}</div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-label">已放款</div>
        <div class="stat-value" style="color: var(--color-success)">{{ stats.granted }}</div>
      </div>
      <div class="stat-card glass-card">
        <div class="stat-label">还款中</div>
        <div class="stat-value" style="color: var(--color-info)">{{ stats.active }}</div>
      </div>
    </div>

    <!-- Table -->
    <div class="glass-card">
      <DataTable :columns="columns" :data="records" :loading="loading" empty-text="暂无贷款记录">
        <template #cell-status="{ row }">
          <span :class="statusClass(row.status)">{{ statusLabel(row.status) }}</span>
        </template>
        <template #cell-amount="{ row }">
          <span class="text-mono">¥{{ formatMoney(row.loanAmount) }}</span>
        </template>
        <template #cell-actions="{ row }">
          <div class="action-btns">
            <router-link :to="`/loans/${row.id}`" class="btn btn-sm btn-ghost">详情</router-link>
            <button v-if="row.status === 'APPROVED'" class="btn btn-sm btn-primary" @click="handleSign(row.id)">签约</button>
          </div>
        </template>
      </DataTable>
      <div v-if="totalPages > 1" class="pagination">
        <button class="btn btn-sm btn-ghost" :disabled="page <= 1" @click="fetchData(page - 1)">上一页</button>
        <span class="page-info">{{ page }} / {{ totalPages }}</span>
        <button class="btn btn-sm btn-ghost" :disabled="page >= totalPages" @click="fetchData(page + 1)">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import DataTable from '@/components/shared/DataTable.vue'
import { getLoans, signLoan } from '@/api/enterprise'
import type { LoanApplication } from '@/types'

const loading = ref(false)
const records = ref<LoanApplication[]>([])
const page = ref(1)
const total = ref(0)
const size = 6
const statusFilter = ref('')
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size)))

const stats = reactive({ total: 0, pending: 0, granted: 0, active: 0 })

const columns = [
  { key: 'id', label: '编号', width: '80px' },
  { key: 'amount', label: '金额' },
  { key: 'loanTerm', label: '期限(月)' },
  { key: 'interestRate', label: '年利率' },
  { key: 'status', label: '状态' },
  { key: 'applyDate', label: '申请日期' },
  { key: 'actions', label: '操作', width: '160px' },
]

async function fetchData(p: number) {
  loading.value = true
  try {
    const res = await getLoans({ page: p, size, status: statusFilter.value || undefined })
    records.value = res.data.records
    total.value = res.data.total
    page.value = p
    // Calc stats
    const all = records.value
    stats.total = total.value
    stats.pending = all.filter(r => r.status === 'PENDING').length
    stats.granted = all.filter(r => r.status === 'GRANTED').length
    stats.active = all.filter(r => r.status === 'GRANTED' || r.status === 'APPROVED').length
  } finally { loading.value = false }
}

async function handleSign(id: number) {
  if (!confirm('确认签约此贷款合同？')) return
  await signLoan(id)
  fetchData(page.value)
}

function statusLabel(s: string) {
  const map: Record<string, string> = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已拒绝', GRANTED: '已放款', REPAID: '已还清', OVERDUE: '已逾期' }
  return map[s] || s
}

function statusClass(s: string) {
  return `badge badge-${s.toLowerCase()}`
}

function formatMoney(v: number) { return v?.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }

onMounted(() => fetchData(1))
</script>

<style scoped>
.page { padding: var(--space-8); }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-8); flex-wrap: wrap; gap: var(--space-4); }
.page-title { font-size: var(--text-2xl); font-weight: 700; color: var(--color-text); }
.page-desc { margin-top: var(--space-1); font-size: var(--text-sm); color: var(--color-text-muted); }
.page-actions { display: flex; gap: var(--space-3); }
.form-select { padding: var(--space-2) var(--space-4); background: var(--color-surface); border: var(--border-glass); border-radius: var(--radius-md); color: var(--color-text); font-size: var(--text-sm); outline: none; }
.btn { display: inline-flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-4); border-radius: var(--radius-md); font-size: var(--text-sm); font-weight: 500; transition: all var(--duration-fast); text-decoration: none; border: none; cursor: pointer; }
.btn-primary { background: var(--color-primary); color: #fff; }
.btn-ghost { background: transparent; color: var(--color-text-secondary); }
.btn-sm { padding: var(--space-1) var(--space-3); font-size: var(--text-xs); }
.btn:disabled { opacity: 0.3; cursor: not-allowed; }
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-4); margin-bottom: var(--space-6); }
.stat-card { padding: var(--space-5); text-align: center; }
.stat-label { font-size: var(--text-xs); color: var(--color-text-muted); text-transform: uppercase; letter-spacing: 0.05em; }
.stat-value { font-size: var(--text-2xl); font-weight: 700; font-family: var(--font-mono); margin-top: var(--space-2); }
.text-mono { font-family: var(--font-mono); }
.action-btns { display: flex; gap: var(--space-2); }
.pagination { display: flex; align-items: center; justify-content: center; gap: var(--space-3); padding: var(--space-4); }
.page-info { font-size: var(--text-sm); color: var(--color-text-muted); }
.badge { display: inline-block; padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.badge-pending { background: rgba(245,158,11,.15); color: var(--color-warning); }
.badge-approved { background: rgba(59,130,246,.15); color: var(--color-info); }
.badge-rejected { background: rgba(239,68,68,.15); color: var(--color-danger); }
.badge-granted { background: rgba(16,185,129,.15); color: var(--color-success); }
.badge-repaid { background: rgba(99,102,241,.15); color: var(--color-primary); }
.badge-overdue { background: rgba(239,68,68,.2); color: var(--color-danger); }
@media (max-width: 768px) { .stats-row { grid-template-columns: repeat(2, 1fr); } }
</style>
