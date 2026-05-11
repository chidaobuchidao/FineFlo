<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">还款计划</h2>
        <p class="page-desc">查看和管理还款计划</p>
      </div>
    </div>

    <div class="glass-card" style="padding: var(--space-6); margin-bottom: var(--space-5);">
      <label class="form-label">选择贷款</label>
      <select v-model="selectedLoanId" class="form-input" style="max-width: 400px;" @change="loadRepayments">
        <option value="">请选择</option>
        <option v-for="l in loanList" :key="l.id" :value="l.id">#{{ l.id }} — ¥{{ formatMoney(l.loanAmount) }} ({{ formatDate(l.applyDate) }})</option>
      </select>
    </div>

    <div v-if="repayments.length" class="glass-card">
      <DataTable :columns="columns" :data="repayments" empty-text="暂无还款记录">
        <template #cell-status="{ row }">
          <span :class="row.status === 'PAID' ? 'badge badge-paid' : 'badge badge-unpaid'">
            {{ row.status === 'PAID' ? '已还款' : row.status === 'OVERDUE' ? '已逾期' : '待还款' }}
          </span>
        </template>
        <template #cell-amount="{ row }">¥{{ formatMoney(row.amount) }}</template>
        <template #cell-paidAmount="{ row }">{{ row.paidAmount ? '¥' + formatMoney(row.paidAmount) : '—' }}</template>
        <template #cell-dueDate="{ row }">{{ formatDate(row.dueDate) }}</template>
        <template #cell-paidDate="{ row }">{{ row.paidDate ? formatDate(row.paidDate) : '—' }}</template>
        <template #cell-actions="{ row }">
          <button v-if="row.status === 'UNPAID'" class="btn btn-sm btn-primary" @click="handlePay(row)">还款</button>
        </template>
      </DataTable>
    </div>
    <p v-else-if="selectedLoanId" class="empty">暂无还款记录</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DataTable from '@/components/shared/DataTable.vue'
import { getRepayments, payRepayment, getLoans } from '@/api/enterprise'
import type { Repayment, LoanApplication } from '@/types'

const route = useRoute()
const selectedLoanId = ref<number | string>('')
const loanList = ref<LoanApplication[]>([])
const repayments = ref<Repayment[]>([])

const columns = [
  { key: 'periodNo', label: '期数' },
  { key: 'amount', label: '应还金额' },
  { key: 'paidAmount', label: '实还金额' },
  { key: 'dueDate', label: '到期日' },
  { key: 'paidDate', label: '还款日' },
  { key: 'status', label: '状态' },
  { key: 'actions', label: '操作', width: '80px' },
]

async function loadRepayments() {
  if (!selectedLoanId.value) { repayments.value = []; return }
  const res = await getRepayments({ loanId: Number(selectedLoanId.value) })
  repayments.value = res.data as unknown as Repayment[]
}

async function handlePay(r: Repayment) {
  if (!confirm(`确认支付第 ${r.periodNo} 期，金额 ¥${formatMoney(r.amount)}？`)) return
  await payRepayment(r.id)
  loadRepayments()
}

function formatMoney(v: number) { return v?.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }
function formatDate(v: string) { return v ? new Date(v).toLocaleDateString('zh-CN') : '—' }

onMounted(async () => {
  const res = await getLoans({ page: 1, size: 100 })
  loanList.value = res.data.records
  // Auto-select from URL
  const q = route.query.loanId
  if (q) { selectedLoanId.value = Number(q); loadRepayments() }
})
</script>

<style scoped>
.page { padding: var(--space-8); }
.page-title { font-size: var(--text-2xl); font-weight: 700; color: var(--color-text); }
.page-desc { margin-top: var(--space-1); font-size: var(--text-sm); color: var(--color-text-muted); margin-bottom: var(--space-6); }
.form-label { display: block; font-size: var(--text-sm); font-weight: 500; color: var(--color-text-secondary); margin-bottom: var(--space-2); }
.form-input { width: 100%; padding: var(--space-3) var(--space-4); background: var(--color-surface); border: var(--border-glass); border-radius: var(--radius-md); color: var(--color-text); font-size: var(--text-base); outline: none; }
.btn { display: inline-flex; align-items: center; padding: var(--space-1) var(--space-3); border-radius: var(--radius-md); font-size: var(--text-xs); font-weight: 500; transition: all var(--duration-fast); border: none; cursor: pointer; }
.btn-primary { background: var(--color-primary); color: #fff; }
.badge { display: inline-block; padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.badge-unpaid { background: rgba(245,158,11,.15); color: var(--color-warning); }
.badge-paid { background: rgba(16,185,129,.15); color: var(--color-success); }
.empty { text-align: center; padding: var(--space-10); color: var(--color-text-muted); }
</style>
