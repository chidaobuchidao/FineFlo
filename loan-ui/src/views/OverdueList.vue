<template>
  <div class="overdue-list">
    <DataTable :columns="columns" :data="store.overdues" :loading="store.loading" :total="store.overdueTotal" :currentPage="page" :pageSize="size">
      <template #cell-overdueAmount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-penalty="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-status="{ value }">
        <span :class="['status-badge', (value as string) === 'ACTIVE' ? 'danger' : 'success']">{{ value === 'ACTIVE' ? '进行中' : '已结清' }}</span>
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
const page = ref(1)
const size = 10

const columns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'enterpriseName', label: '企业' },
  { key: 'loanId', label: '贷款ID' },
  { key: 'overdueDays', label: '逾期天数' },
  { key: 'overdueAmount', label: '逾期金额' },
  { key: 'penalty', label: '罚息' },
  { key: 'startDate', label: '开始日期' },
  { key: 'status', label: '状态' },
]

onMounted(() => store.fetchApprovalOverdues(page.value, size))
</script>

<style scoped>
.status-badge { padding: 2px 8px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.status-badge.success { background: rgba(16,185,129,0.15); color: var(--color-success); }
.status-badge.danger { background: rgba(239,68,68,0.1); color: var(--color-danger); }
</style>
