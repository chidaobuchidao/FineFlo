<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">逾期记录</h2>
        <p class="page-desc">查看历史逾期记录及罚息信息</p>
      </div>
    </div>

    <div class="glass-card">
      <DataTable :columns="columns" :data="overdues" :loading="loading" empty-text="无逾期记录，信用良好">
        <template #cell-status="{ row }">
          <span :class="row.status === 'ACTIVE' ? 'badge badge-active' : 'badge badge-settled'">
            {{ row.status === 'ACTIVE' ? '进行中' : '已结清' }}
          </span>
        </template>
        <template #cell-overdueAmount="{ row }">¥{{ formatMoney(row.overdueAmount) }}</template>
        <template #cell-penalty="{ row }">¥{{ formatMoney(row.penalty) }}</template>
        <template #cell-overdueDays="{ row }">
          <span style="color: var(--color-danger); font-weight: 600;">{{ row.overdueDays }} 天</span>
        </template>
        <template #cell-startDate="{ row }">{{ formatDate(row.startDate) }}</template>
        <template #cell-endDate="{ row }">{{ row.endDate ? formatDate(row.endDate) : '—' }}</template>
      </DataTable>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DataTable from '@/components/shared/DataTable.vue'
import { getOverdues } from '@/api/enterprise'
import type { Overdue } from '@/types'

const loading = ref(false)
const overdues = ref<Overdue[]>([])

const columns = [
  { key: 'loanId', label: '贷款编号' },
  { key: 'overdueDays', label: '逾期天数' },
  { key: 'overdueAmount', label: '逾期金额' },
  { key: 'penalty', label: '罚息' },
  { key: 'startDate', label: '开始日期' },
  { key: 'endDate', label: '结清日期' },
  { key: 'status', label: '状态' },
]

function formatMoney(v: number) { return v?.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }
function formatDate(v: string) { return v ? new Date(v).toLocaleDateString('zh-CN') : '—' }

onMounted(async () => {
  loading.value = true
  try {
    const res = await getOverdues()
    overdues.value = res.data as unknown as Overdue[]
  } finally { loading.value = false }
})
</script>

<style scoped>
.page { padding: var(--space-8); }
.page-title { font-size: var(--text-2xl); font-weight: 700; color: var(--color-text); }
.page-desc { margin-top: var(--space-1); font-size: var(--text-sm); color: var(--color-text-muted); margin-bottom: var(--space-6); }
.badge { display: inline-block; padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.badge-active { background: rgba(239,68,68,.2); color: var(--color-danger); }
.badge-settled { background: rgba(16,185,129,.15); color: var(--color-success); }
</style>
