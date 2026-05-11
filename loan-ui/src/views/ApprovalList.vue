<template>
  <div class="approval-list">
    <div class="page-toolbar">
      <div class="status-filters">
        <button v-for="f in filters" :key="f.value" class="filter-btn pressable" :class="{ active: statusFilter === f.value }" @click="setFilter(f.value)">{{ f.label }}</button>
      </div>
    </div>

    <ErrorBanner
      v-if="error"
      :message="error"
      dismissable
      @retry="doFetch"
      @dismiss="error = ''"
    />

    <EmptyState
      v-if="!store.loading && store.loans.length === 0 && !error"
      icon="search"
      title="暂无贷款审批"
      description="当前没有贷款审批记录"
    />

    <DataTable
      v-if="!store.loading && (store.loans.length > 0 || error)"
      :columns="columns"
      :data="store.loans"
      :loading="store.loading"
      :total="store.loanTotal"
      :currentPage="page"
      :pageSize="size"
      @row-click="goDetail"
    >
      <template #cell-status="{ value }">
        <span class="status-badge" :class="statusClass(value as string)">{{ statusLabel(value as string) }}</span>
      </template>
      <template #cell-loanAmount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-creditScore="{ value }">
        <span :style="{ color: (value as number) >= 70 ? 'var(--color-success)' : (value as number) >= 40 ? 'var(--color-warning)' : 'var(--color-danger)' }">{{ value ?? '-' }}</span>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DataTable from '@/components/shared/DataTable.vue'
import ErrorBanner from '@/components/shared/ErrorBanner.vue'
import EmptyState from '@/components/shared/EmptyState.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import { useApprovalStore } from '@/stores/approval'

const router = useRouter()
const store = useApprovalStore()
const statusFilter = ref('')
const page = ref(1)
const size = 10
const error = ref('')

const filters = [
  { label: '全部', value: '' },
  { label: '待审批', value: 'PENDING' },
  { label: '已审批', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
  { label: '已放款', value: 'GRANTED' },
]

const columns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'enterpriseName', label: '企业' },
  { key: 'loanAmount', label: '贷款金额' },
  { key: 'loanTerm', label: '期限(月)' },
  { key: 'loanPurpose', label: '用途' },
  { key: 'creditScore', label: 'AI评分' },
  { key: 'status', label: '状态' },
  { key: 'applyDate', label: '申请日期' },
]

async function doFetch() {
  error.value = ''
  try {
    await store.fetchApprovalLoans(page.value, size, statusFilter.value)
  } catch {
    error.value = '获取审批列表失败，请重试'
  }
}

function setFilter(v: string) {
  statusFilter.value = v
  page.value = 1
  doFetch()
}

function goDetail(row: Record<string, unknown>) {
  router.push(`/approvals/${row.id}`)
}

function statusClass(s: string): string {
  const map: Record<string, string> = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', GRANTED: 'info', REPAID: 'success', OVERDUE: 'danger' }
  return map[s] || ''
}

function statusLabel(s: string): string {
  const map: Record<string, string> = { PENDING: '待审批', APPROVED: '已审批', REJECTED: '已驳回', GRANTED: '已放款', REPAID: '已还清', OVERDUE: '逾期' }
  return map[s] || s
}

onMounted(() => doFetch())
</script>

<style scoped>
.page-toolbar { margin-bottom: var(--space-6); }
.status-filters { display: flex; gap: var(--space-2); }
.filter-btn { padding: var(--space-1) var(--space-4); border-radius: var(--radius-full); border: var(--border-glass); background: transparent; color: var(--color-text-secondary); font-size: var(--text-sm); transition: all var(--duration-fast) var(--ease-out-expo); cursor: pointer; }
.filter-btn.active { background: var(--color-primary-dim); color: var(--color-primary-hover); border-color: var(--color-primary); }
.filter-btn:hover:not(.active) { background: var(--color-surface-hover); }
.status-badge { padding: 2px 8px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.status-badge.success { background: rgba(16,185,129,0.15); color: var(--color-success); }
.status-badge.danger { background: rgba(239,68,68,0.1); color: var(--color-danger); }
.status-badge.warning { background: rgba(245,158,11,0.15); color: var(--color-warning); }
.status-badge.info { background: rgba(59,130,246,0.15); color: var(--color-info); }
</style>
