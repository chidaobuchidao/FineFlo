<template>
  <div class="disbursement-list">
    <div class="page-toolbar">
      <div class="status-filters">
        <button v-for="f in filters" :key="f.value" class="filter-btn pressable" :class="{ active: statusFilter === f.value }" @click="setFilter(f.value)">{{ f.label }}</button>
      </div>
    </div>
    <DataTable :columns="columns" :data="store.disbursements" :loading="store.loading">
      <template #cell-loanAmount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-interestRate="{ value }">{{ ((value as number) * 100).toFixed(2) }}%</template>
      <template #cell-actions="{ row }">
        <button v-if="(row as any).status === 'APPROVED'" class="action-btn pressable" @click.stop="handleGrant((row as any).id)">确认放款</button>
        <span v-else class="text-muted">已放款</span>
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
  { label: '待放款', value: 'PENDING' },
  { label: '已放款', value: 'GRANTED' },
]

const columns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'enterpriseName', label: '企业' },
  { key: 'loanAmount', label: '金额' },
  { key: 'loanTerm', label: '期限(月)' },
  { key: 'interestRate', label: '利率' },
  { key: 'approveDate', label: '审批日期' },
  { key: 'actions', label: '操作', width: '120px' },
]

function setFilter(v: string) { statusFilter.value = v; store.fetchDisbursements(v) }
async function handleGrant(loanId: number) { const ok = await store.grantDisbursement(loanId); if (ok) store.fetchDisbursements(statusFilter.value) }

onMounted(() => store.fetchDisbursements())
</script>

<style scoped>
.page-toolbar { margin-bottom: var(--space-6); }
.status-filters { display: flex; gap: var(--space-2); }
.filter-btn { padding: var(--space-1) var(--space-4); border-radius: var(--radius-full); border: var(--border-glass); background: transparent; color: var(--color-text-secondary); font-size: var(--text-sm); }
.filter-btn.active { background: var(--color-primary-dim); color: var(--color-primary-hover); border-color: var(--color-primary); }
.action-btn { padding: 4px 12px; border-radius: var(--radius-sm); border: none; background: var(--color-primary); color: white; font-size: var(--text-xs); font-weight: 500; }
.action-btn:hover { filter: brightness(1.1); }
.text-muted { color: var(--color-text-muted); font-size: var(--text-sm); }
</style>
