<template>
  <div class="repayment-list">
    <div class="page-toolbar">
      <div class="status-filters">
        <button v-for="f in filters" :key="f.value" class="filter-btn pressable" :class="{ active: statusFilter === f.value }" @click="setFilter(f.value)">{{ f.label }}</button>
      </div>
    </div>
    <DataTable :columns="columns" :data="store.repayments" :loading="store.loading">
      <template #cell-amount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-paidAmount="{ value }">¥{{ (value as number)?.toLocaleString() || '-' }}</template>
      <template #cell-status="{ value }">
        <span :class="['status-badge', statusClass(value as string)]">{{ statusLabel(value as string) }}</span>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DataTable from '@/components/shared/DataTable.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import { useApprovalStore } from '@/stores/approval'

const store = useApprovalStore()
const statusFilter = ref('')

const filters = [
  { label: '全部', value: '' },
  { label: '未还', value: 'UNPAID' },
  { label: '已还', value: 'PAID' },
  { label: '逾期', value: 'OVERDUE' },
]

const columns: Column[] = [
  { key: 'loanId', label: '贷款ID' },
  { key: 'periodNo', label: '期数' },
  { key: 'amount', label: '应还金额' },
  { key: 'paidAmount', label: '实还金额' },
  { key: 'dueDate', label: '到期日' },
  { key: 'paidDate', label: '还款日' },
  { key: 'status', label: '状态' },
]

function setFilter(v: string) { statusFilter.value = v; store.fetchApprovalRepayments(undefined, v) }
function statusClass(s: string): string { const map: Record<string, string> = { PAID: 'success', OVERDUE: 'danger', UNPAID: 'warning' }; return map[s] || '' }
function statusLabel(s: string): string { const map: Record<string, string> = { PAID: '已还', OVERDUE: '逾期', UNPAID: '未还' }; return map[s] || s }

onMounted(() => store.fetchApprovalRepayments())
</script>

<style scoped>
.page-toolbar { margin-bottom: var(--space-6); }
.status-filters { display: flex; gap: var(--space-2); }
.filter-btn { padding: var(--space-1) var(--space-4); border-radius: var(--radius-full); border: var(--border-glass); background: transparent; color: var(--color-text-secondary); font-size: var(--text-sm); }
.filter-btn.active { background: var(--color-primary-dim); color: var(--color-primary-hover); border-color: var(--color-primary); }
.status-badge { padding: 2px 8px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.status-badge.success { background: rgba(16,185,129,0.15); color: var(--color-success); }
.status-badge.danger { background: rgba(239,68,68,0.1); color: var(--color-danger); }
.status-badge.warning { background: rgba(245,158,11,0.15); color: var(--color-warning); }
</style>
